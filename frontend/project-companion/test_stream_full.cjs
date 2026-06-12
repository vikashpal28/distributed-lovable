const http = require('http');

async function testStream() {
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
  const loginData = JSON.parse(await res.text());
  const token = loginData.token;
  
  res = await fetch(`${baseUrl}/api/v1/workspace/projects`, {
    method: 'POST',
    headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
    body: JSON.stringify({ name: "My Stream Test Project Full" })
  });
  const proj = JSON.parse(await res.text());
  const projectId = proj.id;

  res = await fetch(`${baseUrl}/api/v1/intelligence/chat/stream`, {
    method: 'POST',
    headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
    body: JSON.stringify({ message: "Hello", projectId: Number(projectId) }),
  });
  
  const reader = res.body.getReader();
  const decoder = new TextDecoder();
  while(true) {
    const {done, value} = await reader.read();
    if(done) break;
  }
  
  res = await fetch(`${baseUrl}/api/v1/intelligence/chat/projects/${projectId}/chat`, {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  const finalHistory = await res.text();
  console.log(`Final History:`, finalHistory);
}

testStream();
