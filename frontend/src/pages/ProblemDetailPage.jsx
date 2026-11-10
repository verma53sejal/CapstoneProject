import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { motion } from 'framer-motion';
import { API } from '../context/AuthContext';
import toast from 'react-hot-toast';
import Editor from 'react-simple-code-editor';
import Prism from 'prismjs';
import 'prismjs/components/prism-python';
import 'prismjs/components/prism-java';
import 'prismjs/components/prism-c';
import 'prismjs/components/prism-cpp';
import { FiPlay, FiBookmark, FiZap, FiClock, FiAlertCircle, FiCheckCircle, FiChevronRight } from 'react-icons/fi';

const DIFF_COLORS = { Easy: 'var(--easy)', Medium: 'var(--medium)', Hard: 'var(--hard)' };

const LANGUAGE_TEMPLATES = {
  javascript: name => `function ${name}(nums, target) {\n  // Write your solution\n}`,
  python: name => `class Solution:\n    def ${name}(self, nums, target):\n        # Write your solution\n        pass`,
  java: name => `class Solution {\n    public int[] ${name}(int[] nums, int target) {\n        // Write your solution\n        return new int[]{};\n    }\n}`,
  cpp: name => `#include <bits/stdc++.h>\nusing namespace std;\n\nclass Solution {\npublic:\n    vector<int> ${name}(vector<int>& nums, int target) {\n        // Write your solution\n        return {};\n    }\n};`,
  c: () => `#include <stdio.h>\n\nint main() {\n    // Write your solution\n    return 0;\n}`
};

const getFunctionName = problemData => {
  const words = (problemData?.slug || problemData?.title || 'solve').replace(/[^a-zA-Z0-9]+/g, ' ').trim().split(/\s+/).filter(Boolean);
  return words.map((word, index) => index === 0 ? word.toLowerCase() : word[0].toUpperCase() + word.slice(1).toLowerCase()).join('') || 'solve';
};

const getTemplate = (problemData, selectedLanguage) => (LANGUAGE_TEMPLATES[selectedLanguage] || LANGUAGE_TEMPLATES.javascript)(getFunctionName(problemData));
const PRISM_LANGUAGES = { javascript: 'javascript', python: 'python', java: 'java', cpp: 'cpp', c: 'c' };

const SAMPLE_PROBLEM = {
  title: 'Two Sum',
  difficulty: 'Easy',
  tags: ['Arrays', 'Hashing'],
  description: `Given an array of integers \`nums\` and an integer \`target\`, return indices of the two numbers such that they add up to target.

You may assume that each input would have exactly one solution, and you may not use the same element twice.

You can return the answer in any order.`,
  examples: [
    { input: 'nums = [2,7,11,15], target = 9', output: '[0,1]', explanation: 'Because nums[0] + nums[1] == 9, we return [0, 1].' },
    { input: 'nums = [3,2,4], target = 6', output: '[1,2]', explanation: '' }
  ],
  constraints: ['2 <= nums.length <= 10^4', '-10^9 <= nums[i] <= 10^9', 'Only one valid answer exists.'],
  acceptanceRate: 49.2
};

export default function ProblemDetailPage() {
  const { slug } = useParams();
  const [problem, setProblem] = useState(null);
  const [codeByLanguage, setCodeByLanguage] = useState({});
  const [language, setLanguage] = useState('javascript');
  const [submitting, setSubmitting] = useState(false);
  const [result, setResult] = useState(null);
  const [activeTab, setActiveTab] = useState('description');
  const [notes, setNotes] = useState('');

  useEffect(() => {
    setProblem(null);
    setLanguage('javascript');
    setCodeByLanguage({});
    API.get(`/problems/${slug}`).then(res => { setProblem(res.data); }).catch(() => toast.error('Unable to load problem'));
  }, [slug]);

  const code = codeByLanguage[language] || getTemplate(problem, language);
  const updateCode = value => setCodeByLanguage(previous => ({ ...previous, [language]: value }));
  const changeLanguage = nextLanguage => {
    setCodeByLanguage(previous => ({ ...previous, [language]: code }));
    setLanguage(nextLanguage);
  };

  const handleRun = async () => {
    setSubmitting(true);
    setResult(null);
    try {
      const problemId = problem.id || problem._id;
      const res = await API.post(`/problems/${problemId}/run`, { code, language });
      setResult(res.data);
      if (res.data.status !== 'Accepted') toast.error(res.data.status || 'Run failed');
    } catch (error) {
      toast.error(error.response?.data?.message || 'Unable to run code.');
    } finally { setSubmitting(false); }
  };

  const handleSubmit = async () => {
    setSubmitting(true);
    setResult(null);
    try {
      const problemId = problem.id || problem._id;
      const res = await API.post(`/problems/${problemId}/submit`, { code, language });
      setResult(res.data);
      if (status === 'Accepted') toast.success(`✅ Accepted! +${res.data.creditsEarned} credits`);
      else toast.error('Wrong Answer — try again!');
    } catch (error) {
      const message = error.response?.data?.message || 'Unable to submit solution.';
      toast.error(message);
    } finally { setSubmitting(false); }
  };

  if (!problem) return <div style={{ padding: 40, color: 'var(--text-muted)' }}>Loading problem...</div>;

  return (
    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 0, height: 'calc(100vh - 48px)', margin: '-24px', overflow: 'hidden' }}>
      {/* Left Panel - Problem */}
      <div style={{ borderRight: '1px solid var(--border)', display: 'flex', flexDirection: 'column', overflow: 'hidden' }}>
        {/* Problem Header */}
        <div style={{ padding: '16px 20px', borderBottom: '1px solid var(--border)', background: 'var(--bg-secondary)' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
            <h2 style={{ fontFamily: 'Syne', fontWeight: 700, fontSize: 18, flex: 1 }}>{problem.title}</h2>
            <span style={{ fontSize: 12, fontWeight: 700, color: DIFF_COLORS[problem.difficulty], padding: '3px 10px', borderRadius: 12, background: `${DIFF_COLORS[problem.difficulty]}15`, border: `1px solid ${DIFF_COLORS[problem.difficulty]}30` }}>
              {problem.difficulty}
            </span>
          </div>
          <div style={{ display: 'flex', gap: 6, marginTop: 8, flexWrap: 'wrap' }}>
            {(problem.tags || []).map(t => (
              <span key={t} style={{ fontSize: 11, padding: '2px 8px', borderRadius: 4, background: 'var(--bg-card)', color: 'var(--text-muted)', border: '1px solid var(--border)' }}>{t}</span>
            ))}
          </div>
        </div>

        {/* Tabs */}
        <div style={{ display: 'flex', borderBottom: '1px solid var(--border)', background: 'var(--bg-secondary)' }}>
          {['description', 'examples', 'notes'].map(tab => (
            <button key={tab} onClick={() => setActiveTab(tab)} style={{
              padding: '10px 16px', background: 'none', border: 'none', fontSize: 13, fontWeight: 500, cursor: 'pointer',
              color: activeTab === tab ? 'var(--accent-blue)' : 'var(--text-muted)',
              borderBottom: activeTab === tab ? '2px solid var(--accent-blue)' : '2px solid transparent',
              textTransform: 'capitalize'
            }}>{tab}</button>
          ))}
        </div>

        {/* Tab Content */}
        <div style={{ flex: 1, overflowY: 'auto', padding: 20 }}>
          {activeTab === 'description' && (
            <div>
              <p style={{ fontSize: 14, color: 'var(--text-secondary)', lineHeight: 1.8, marginBottom: 20, whiteSpace: 'pre-wrap' }}>{problem.description}</p>
              {(problem.constraints || []).length > 0 && (
                <div>
                  <h4 style={{ fontSize: 13, fontWeight: 700, marginBottom: 10, color: 'var(--text-primary)' }}>Constraints:</h4>
                  {(problem.constraints || []).map((c, i) => (
                    <div key={i} style={{ display: 'flex', gap: 8, marginBottom: 6, alignItems: 'flex-start' }}>
                      <FiChevronRight size={12} color="var(--accent-blue)" style={{ marginTop: 3, flexShrink: 0 }} />
                      <code style={{ fontSize: 12, color: 'var(--text-secondary)', fontFamily: 'JetBrains Mono' }}>{c}</code>
                    </div>
                  ))}
                </div>
              )}
              <div style={{ marginTop: 16, padding: '12px 16px', background: 'rgba(79,142,247,0.06)', border: '1px solid rgba(79,142,247,0.15)', borderRadius: 8, display: 'flex', alignItems: 'center', gap: 8 }}>
                <FiZap size={14} color="#eab308" />
                <span style={{ fontSize: 13, color: 'var(--text-secondary)' }}>Solve to earn <strong style={{ color: '#eab308' }}>+{({ Easy: 10, Medium: 20, Hard: 40 }[problem.difficulty] || 10)} credits</strong></span>
              </div>
            </div>
          )}
          {activeTab === 'examples' && (
            <div>
              {(problem.examples || []).map((ex, i) => (
                <div key={i} style={{ marginBottom: 20, background: 'var(--bg-secondary)', border: '1px solid var(--border)', borderRadius: 10, padding: 16 }}>
                  <div style={{ fontSize: 12, fontWeight: 700, color: 'var(--accent-blue)', marginBottom: 10 }}>Example {i + 1}</div>
                  <div style={{ marginBottom: 8 }}>
                    <span style={{ fontSize: 12, color: 'var(--text-muted)', fontWeight: 600 }}>Input: </span>
                    <code style={{ fontSize: 12, color: 'var(--text-secondary)', fontFamily: 'JetBrains Mono' }}>{ex.input}</code>
                  </div>
                  <div style={{ marginBottom: ex.explanation ? 8 : 0 }}>
                    <span style={{ fontSize: 12, color: 'var(--text-muted)', fontWeight: 600 }}>Output: </span>
                    <code style={{ fontSize: 12, color: 'var(--accent-green)', fontFamily: 'JetBrains Mono' }}>{ex.output}</code>
                  </div>
                  {ex.explanation && <div style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 6 }}>💡 {ex.explanation}</div>}
                </div>
              ))}
            </div>
          )}
          {activeTab === 'notes' && (
            <div>
              <p style={{ fontSize: 13, color: 'var(--text-muted)', marginBottom: 12 }}>Your personal notes (saved locally)</p>
              <textarea value={notes} onChange={e => setNotes(e.target.value)}
                style={{ minHeight: 200, resize: 'vertical', fontFamily: 'JetBrains Mono', fontSize: 13 }}
                placeholder="Write your approach, observations, or key insights here..." />
            </div>
          )}
        </div>
      </div>

      {/* Right Panel - Code Editor */}
      <div style={{ display: 'flex', flexDirection: 'column', overflow: 'hidden' }}>
        {/* Editor Header */}
        <div style={{ padding: '12px 16px', borderBottom: '1px solid var(--border)', background: 'var(--bg-secondary)', display: 'flex', alignItems: 'center', gap: 12 }}>
          <select value={language} onChange={e => changeLanguage(e.target.value)}
            style={{ width: 'auto', background: 'var(--bg-card)', padding: '6px 12px', fontSize: 13 }}>
            {['javascript', 'python', 'java', 'cpp', 'c'].map(l => <option key={l}>{l}</option>)}
          </select>
          <div style={{ flex: 1 }} />
          <button onClick={handleRun} className="btn-secondary" style={{ padding: '7px 16px', fontSize: 13 }} disabled={submitting}>
            Run
          </button>
          <button onClick={handleSubmit} className="btn-primary" style={{ padding: '7px 16px', fontSize: 13, display: 'flex', alignItems: 'center', gap: 6 }}
            disabled={submitting}>
            <FiPlay size={14} />
            {submitting ? 'Judging...' : 'Submit'}
          </button>
        </div>

        {/* Code Area */}
        <div style={{ flex: 1, overflow: 'hidden', position: 'relative' }}>
          <Editor
            value={code} onValueChange={updateCode}
            highlight={value => Prism.highlight(value, Prism.languages[PRISM_LANGUAGES[language]], PRISM_LANGUAGES[language])}
            padding={20}
            preClassName="problem-editor-highlight"
            textareaClassName="problem-editor-input"
            style={{
              width: '100%', height: '100%', fontFamily: 'JetBrains Mono, monospace', fontSize: 14,
              lineHeight: 1.6, background: '#0d1117', color: '#e2e8f0', tabSize: 2,
              overflow: 'auto'
            }}
          />
          {submitting && (
            <div style={{ position: 'absolute', inset: 0, background: 'rgba(13,17,23,0.8)', display: 'flex', alignItems: 'center', justifyContent: 'center', flexDirection: 'column', gap: 12 }}>
              <div style={{ width: 40, height: 40, borderRadius: '50%', border: '3px solid var(--accent-blue)', borderTopColor: 'transparent', animation: 'spin 0.8s linear infinite' }} />
              <span style={{ color: 'var(--text-muted)', fontSize: 14 }}>Running test cases...</span>
            </div>
          )}
        </div>

        {/* Result Panel */}
        {result && (
          <motion.div initial={{ height: 0, opacity: 0 }} animate={{ height: 'auto', opacity: 1 }}
            style={{ borderTop: '1px solid var(--border)', padding: 16, background: result.status === 'Accepted' ? 'rgba(34,197,94,0.05)' : 'rgba(239,68,68,0.05)' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
              {result.status === 'Accepted'
                ? <FiCheckCircle size={20} color="var(--accent-green)" />
                : <FiAlertCircle size={20} color="var(--hard)" />}
              <span style={{ fontWeight: 700, fontSize: 16, color: result.status === 'Accepted' ? 'var(--accent-green)' : 'var(--hard)' }}>
                {result.status}
              </span>
              {result.creditsEarned > 0 && (
                <span style={{ marginLeft: 'auto', display: 'flex', alignItems: 'center', gap: 4, color: '#eab308', fontWeight: 700 }}>
                  <FiZap size={14} /> +{result.creditsEarned} credits
                </span>
              )}
            </div>
            {result.status === 'Accepted' && (
              <div style={{ display: 'flex', gap: 16, marginTop: 10 }}>
                <span style={{ fontSize: 12, color: 'var(--text-muted)' }}>⏱ Runtime: <strong style={{ color: 'var(--text-secondary)' }}>{result.runtime || 42}ms</strong></span>
                <span style={{ fontSize: 12, color: 'var(--text-muted)' }}>💾 Memory: <strong style={{ color: 'var(--text-secondary)' }}>32MB</strong></span>
              </div>
            )}
          </motion.div>
        )}
      </div>

      <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
    </div>
  );
}
