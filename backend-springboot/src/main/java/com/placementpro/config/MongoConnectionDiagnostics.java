package com.placementpro.config;

import org.bson.Document;
import com.mongodb.MongoCommandException;
import com.mongodb.MongoConfigurationException;
import com.mongodb.MongoException;
import com.mongodb.MongoTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.net.UnknownHostException;

/**
 * Verifies the configured MongoDB target at startup without exposing credentials.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MongoConnectionDiagnostics implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(MongoConnectionDiagnostics.class);

    private final MongoTemplate mongoTemplate;
    private final String mongoUri;

    public MongoConnectionDiagnostics(
            MongoTemplate mongoTemplate,
            @Value("${spring.data.mongodb.uri}") String mongoUri) {
        this.mongoTemplate = mongoTemplate;
        this.mongoUri = mongoUri;
    }

    @Override
    public void run(ApplicationArguments args) {
        String target = isAtlasUri(mongoUri) ? "Atlas" : "standard MongoDB";
        String host = safeAuthority(mongoUri);
        String database = mongoTemplate.getDb().getName();

        log.info("MongoDB target configured: type={}, host={}, database={}", target, host, database);

        try {
            mongoTemplate.getDb().runCommand(new Document("ping", 1));
            log.info("MongoDB connectivity verified: ping succeeded (type={}, database={})", target, database);
        } catch (Exception exception) {
            log.error("MongoDB connectivity failed: type={}, host={}, database={}, reason={}",
                    target, host, database, classifyFailure(exception));
        }
    }

    private String classifyFailure(Throwable exception) {
        Throwable current = exception;
        while (current != null) {
            if (current instanceof MongoCommandException commandException
                    && commandException.getErrorCode() == 8000) {
                return "AUTHENTICATION_FAILURE";
            }
            if (current instanceof MongoConfigurationException
                    || current instanceof IllegalArgumentException) {
                return "INVALID_CONNECTION_STRING";
            }
            if (current instanceof UnknownHostException) {
                return "DNS_OR_SRV_RESOLUTION_FAILURE";
            }
            if (current instanceof MongoTimeoutException) {
                return "CONNECTION_TIMEOUT_OR_IP_WHITELIST_FAILURE";
            }
            if (current instanceof MongoException mongoException
                    && mongoException.getMessage() != null
                    && mongoException.getMessage().toLowerCase().contains("authentication")) {
                return "AUTHENTICATION_FAILURE";
            }
            current = current.getCause();
        }
        return exception.getClass().getSimpleName();
    }

    private boolean isAtlasUri(String uri) {
        return uri != null && uri.startsWith("mongodb+srv://");
    }

    private String safeAuthority(String uri) {
        if (uri == null || uri.isBlank()) {
            return "not configured";
        }

        String withoutScheme = uri.replaceFirst("^mongodb(?:\\+srv)?://", "");
        int atIndex = withoutScheme.indexOf('@');
        if (atIndex >= 0) {
            withoutScheme = withoutScheme.substring(atIndex + 1);
        }

        int pathIndex = withoutScheme.indexOf('/');
        int queryIndex = withoutScheme.indexOf('?');
        int endIndex = withoutScheme.length();
        if (pathIndex >= 0) {
            endIndex = Math.min(endIndex, pathIndex);
        }
        if (queryIndex >= 0) {
            endIndex = Math.min(endIndex, queryIndex);
        }
        return withoutScheme.substring(0, endIndex);
    }
}
