
async function testBackend() {
  const baseUrl = 'http://localhost:8080';
  const email = `test_${Date.now()}@test.com`;
  const password = 'password123';

  console.log("1. Signup...");
  try {
    let res = await fetch(`${baseUrl}/api/auth/signup`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password, name: "Test User", username: email })
    });
    console.log("Signup status:", res.status);
    console.log("Signup body:", await res.text());

    console.log("\n2. Login...");
    res = await fetch(`${baseUrl}/api/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username: email, password, email: email })
    });
    console.log("Login status:", res.status);
    const loginText = await res.text();
    console.log("Login body:", loginText);

    let token;
    try {
        const loginData = JSON.parse(loginText);
        token = loginData.token || loginData.jwt || loginData.accessToken;
    } catch(e) {}
    
    if (!token) {
        console.log("Failed to get token. Cannot continue.");
        return;
    }

    console.log("\n3. Get Profile...");
    res = await fetch(`${baseUrl}/api/auth/me`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    console.log("Profile status:", res.status);
    console.log("Profile body:", await res.text());

    console.log("\n4. Create Project...");
    res = await fetch(`${baseUrl}/api/projects`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
      body: JSON.stringify({ name: "My Test Project" })
    });
    console.log("Create Project status:", res.status);
    console.log("Create Project body:", await res.text());

    console.log("\n5. Get Projects...");
    res = await fetch(`${baseUrl}/api/projects`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    console.log("Get Projects status:", res.status);
    console.log("Get Projects body:", await res.text());

  } catch (e) {
    console.error("Error:", e);
  }
}

testBackend();
