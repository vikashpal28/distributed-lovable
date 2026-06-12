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
    body: JSON.stringify({ name: "My Stream Test Project" })
  });
  const proj = JSON.parse(await res.text());
  const projectId = proj.id;

  // Simulate sending a message but just abort it to let backend process it
  // Actually, we can just send the message to the non-streaming chat endpoint if it exists,
  // or we can just get history to see if it starts empty.
  
  res = await fetch(`${baseUrl}/api/v1/intelligence/chat/projects/${projectId}/chat`, {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  console.log(`Initial History:`, await res.text());

  // Wait, let's use the stream endpoint but use a controller
  const controller = new AbortController();
  const fetchPromise = fetch(`${baseUrl}/api/v1/intelligence/chat/stream`, {
    method: 'POST',
    headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
    body: JSON.stringify({ message: "Hello AI!", projectId: Number(projectId) }),
    signal: controller.signal
  });
  
  // Give it 3 seconds to process then abort
  setTimeout(() => controller.abort(), 3000);
  
  try {
    await fetchPromise;
  } catch(e) {}

  res = await fetch(`${baseUrl}/api/v1/intelligence/chat/projects/${projectId}/chat`, {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  const finalHistory = await res.text();
  console.log(`Final History:`, finalHistory);
}

testStream();
