const http = require('http');

async function testBackend() {
  const baseUrl = 'http://localhost:8080';
  const email = `test_${Date.now()}@test.com`;
  const password = 'password123';

  let res = await fetch(`${baseUrl}/api/v1/account/auth/signup`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password, name: "Test User", username: email })
  });
  
  res = await fetch(`${baseUrl}/api/v1/account/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username: email, password, email: email })
  });
  const loginText = await res.text();
  const loginData = JSON.parse(loginText);
  const token = loginData.token;
  
  res = await fetch(`${baseUrl}/api/v1/workspace/projects`, {
    method: 'POST',
    headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
    body: JSON.stringify({ name: "My Test Project" })
  });
  const projText = await res.text();
  const proj = JSON.parse(projText);
  const projectId = proj.id;

  const endpoints = [
    `/api/v1/intelligence/chat/projects/${projectId}/chat`,
    `/api/v1/intelligence/projects/${projectId}/chat`,
    `/api/v1/intelligence/chat/projects/${projectId}`,
    `/api/v1/intelligence/projects/${projectId}`
  ];

  for (const ep of endpoints) {
    res = await fetch(`${baseUrl}${ep}`, { headers: { 'Authorization': `Bearer ${token}` } });
    const text = await res.text();
    console.log(`GET ${ep} -> ${res.status} ${text.substring(0, 100)}`);
  }
}

testBackend();
