import {
  ChatMessage,
  DeployResponse,
  FileNode,
  LoginCredentials,
  LoginResponse,
  ProjectSummaryResponse,
  ProjectRequest,
  ProjectResponse,
  ProjectMember,
  ProjectRole,
  SignupRequest,
  AuthResponse,
  PortalResponse,
  CheckoutRequest,
  CheckoutResponse,
  UsageTodayResponse,
  PlanLimitsResponse,
  PlanResponse,
  SubscriptionResponse,
  UserProfileResponse,
  MemberResponse
} from "./types";

const isProduction = import.meta.env.MODE === "production";

// Force the app to use your API Gateway subdomain in production, 
// and localhost when you are developing on your laptop.
export const BASE_URL = isProduction
  ? "https://api.codegenai.online"
  : "http://localhost:8080";


export const getAuthToken = () => localStorage.getItem("auth_token");
export const setAuthToken = (token: string) => localStorage.setItem("auth_token", token);
export const removeAuthToken = () => localStorage.removeItem("auth_token");
export const isAuthenticated = () => !!getAuthToken();

const getAuthHeaders = (): HeadersInit => {
  const token = getAuthToken();
  return token ? { Authorization: `Bearer ${token}` } : {};
};

export const setUserInfo = (user: { id: number; username: string; name: string }) => {
  localStorage.setItem("user_info", JSON.stringify(user));
};

export const getUserInfo = (): { id: number; username: string; name: string } | null => {
  const userInfo = localStorage.getItem("user_info");
  if (!userInfo || userInfo === "undefined") return null;
  try {
    return JSON.parse(userInfo);
  } catch (e) {
    return null;
  }
};

export const removeUserInfo = () => localStorage.removeItem("user_info");

export const PREVIEW_URL_KEY = "preview_url";
export const OPEN_TABS_KEY = "open_tabs";
export const ACTIVE_TAB_KEY = "active_tab";

function buildFileTree(paths: { path: string }[]): FileNode[] {
  if (!paths || !Array.isArray(paths)) return [];
  const root: FileNode[] = [];
  const nodeMap = new Map<string, FileNode>();

  const sortedPaths = [...paths].sort((a, b) => a.path.localeCompare(b.path));

  for (const { path } of sortedPaths) {
    const parts = path.split("/");
    let currentPath = "";

    for (let i = 0; i < parts.length; i++) {
      const part = parts[i];
      const parentPath = currentPath;
      currentPath = currentPath ? `${currentPath}/${part}` : part;

      if (nodeMap.has(currentPath)) continue;

      const isFile = i === parts.length - 1;
      const node: FileNode = {
        name: part,
        path: currentPath,
        type: isFile ? "file" : "directory",
        children: isFile ? undefined : [],
      };

      nodeMap.set(currentPath, node);

      if (parentPath) {
        const parent = nodeMap.get(parentPath);
        if (parent && parent.children) {
          parent.children.push(node);
        }
      } else {
        root.push(node);
      }
    }
  }

  const sortNodes = (nodes: FileNode[]) => {
    nodes.sort((a, b) => {
      if (a.type === "directory" && b.type === "file") return -1;
      if (a.type === "file" && b.type === "directory") return 1;
      return a.name.localeCompare(b.name);
    });
    nodes.forEach((node) => {
      if (node.children) sortNodes(node.children);
    });
  };

  sortNodes(root);
  return root;
}

export const api = {

  // ─── Account: Auth ───────────────────────────────────────────────────────────

  async login(credentials: LoginCredentials): Promise<LoginResponse> {
    const response = await fetch(`${BASE_URL}/api/v1/account/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(credentials),
    });
    if (!response.ok) {
      const error = await response.text();
      throw new Error(error || "Login failed");
    }
    return response.json();
  },

  async signup(data: SignupRequest): Promise<AuthResponse> {
    const response = await fetch(`${BASE_URL}/api/v1/account/auth/signup`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    });
    if (!response.ok) {
      const error = await response.text();
      throw new Error(error || "Signup failed");
    }
    return response.json();
  },

  // ─── Account: User / Profile ─────────────────────────────────────────────────

  async getProfile(): Promise<UserProfileResponse> {
    const response = await fetch(`${BASE_URL}/api/v1/account/auth/me`, {
      headers: { ...getAuthHeaders() },
    });
    if (!response.ok) throw new Error("Failed to fetch user profile");
    const text = await response.text();
    if (!text) return null as any;
    try {
      return JSON.parse(text);
    } catch (e) {
      return null as any;
    }
  },

  // ─── Account: Plans ──────────────────────────────────────────────────────────

  async getAllPlans(): Promise<PlanResponse[]> {
    const response = await fetch(`${BASE_URL}/api/v1/account/plan`, {
      headers: { ...getAuthHeaders() },
    });
    if (!response.ok) throw new Error("Failed to fetch plans");
    return response.json();
  },

  async getPlanLimits(): Promise<PlanLimitsResponse> {
    const response = await fetch(`${BASE_URL}/api/v1/account/plan/limits`, {
      headers: { ...getAuthHeaders() },
    });
    if (!response.ok) throw new Error("Failed to fetch plan limits");
    return response.json();
  },

  async getTodayUsage(): Promise<UsageTodayResponse> {
    const response = await fetch(`${BASE_URL}/api/v1/account/plan/usage/today`, {
      headers: { ...getAuthHeaders() },
    });
    if (!response.ok) throw new Error("Failed to fetch today usage");
    return response.json();
  },

  // ─── Account: Subscription & Payments ────────────────────────────────────────

  async getMySubscription(): Promise<SubscriptionResponse> {
    const response = await fetch(`${BASE_URL}/api/v1/account/api/me/subscription`, {
      headers: { ...getAuthHeaders() },
    });
    if (!response.ok) throw new Error("Failed to fetch subscription");
    return response.json();
  },

  async openCustomerPortal(): Promise<PortalResponse> {
    const response = await fetch(`${BASE_URL}/api/v1/account/api/payments/portal`, {
      method: "POST",
      headers: { ...getAuthHeaders() },
    });
    if (!response.ok) throw new Error("Failed to open customer portal");
    return response.json();
  },

  async createCheckoutSession(planId: number, successUrl?: string, cancelUrl?: string): Promise<CheckoutResponse> {
    const response = await fetch(`${BASE_URL}/api/v1/account/api/payments/checkout`, {
      method: "POST",
      headers: { "Content-Type": "application/json", ...getAuthHeaders() },
      body: JSON.stringify({ planId, successUrl, cancelUrl }),
    });
    if (!response.ok) throw new Error("Failed to create checkout session");
    return response.json();
  },

  // ─── Workspace: Projects ─────────────────────────────────────────────────────

  async getProjects(): Promise<ProjectSummaryResponse[]> {
    const response = await fetch(`${BASE_URL}/api/v1/workspace/projects?_t=${Date.now()}`, {
      headers: {
        ...getAuthHeaders(),
        'Cache-Control': 'no-cache, no-store, must-revalidate',
        'Pragma': 'no-cache',
        'Expires': '0',
      },
      cache: 'no-store',
    });
    if (!response.ok) throw new Error("Failed to fetch projects");
    const data = await response.json();
    if (Array.isArray(data)) return data;
    if (data?.content && Array.isArray(data.content)) return data.content;
    if (data?.data && Array.isArray(data.data)) return data.data;
    if (data?.projects && Array.isArray(data.projects)) return data.projects;
    if (data?._embedded) {
      for (const key in data._embedded) {
        if (Array.isArray(data._embedded[key])) return data._embedded[key];
      }
    }
    return [];
  },

  async createProject(name: string): Promise<ProjectSummaryResponse> {
    const response = await fetch(`${BASE_URL}/api/v1/workspace/projects`, {
      method: "POST",
      headers: { "Content-Type": "application/json", ...getAuthHeaders() },
      body: JSON.stringify({ name }),
    });
    if (!response.ok) throw new Error("Failed to create project");
    const text = await response.text();
    if (!text) {
      return { id: Date.now(), name, createdAt: new Date().toISOString() } as unknown as ProjectSummaryResponse;
    }
    try {
      return JSON.parse(text);
    } catch (e) {
      if (!isNaN(Number(text))) {
        return { id: Number(text), name, createdAt: new Date().toISOString() } as unknown as ProjectSummaryResponse;
      }
      throw new Error("Invalid project response");
    }
  },

  async getProject(id: string): Promise<ProjectResponse> {
    const response = await fetch(`${BASE_URL}/api/v1/workspace/projects/${id}?_t=${Date.now()}`, {
      headers: { ...getAuthHeaders() },
    });
    if (!response.ok) throw new Error("Failed to fetch project");
    const text = await response.text();
    if (!text) throw new Error("Empty project data");
    try {
      return JSON.parse(text);
    } catch (e) {
      throw new Error("Invalid project JSON");
    }
  },

  async updateProject(id: string, name: string): Promise<ProjectResponse> {
    const response = await fetch(`${BASE_URL}/api/v1/workspace/projects/${id}`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json", ...getAuthHeaders() },
      body: JSON.stringify({ name }),
    });
    if (!response.ok) throw new Error("Failed to update project");
    return response.json();
  },

  async deleteProject(id: string): Promise<void> {
    const response = await fetch(`${BASE_URL}/api/v1/workspace/projects/${id}`, {
      method: "DELETE",
      headers: { ...getAuthHeaders() },
    });
    if (!response.ok) throw new Error("Failed to delete project");
  },

  // ─── Workspace: Project Files ─────────────────────────────────────────────────

  async getFiles(projectId: string): Promise<FileNode[]> {
    const response = await fetch(`${BASE_URL}/api/v1/workspace/projects/${projectId}/files?_t=${Date.now()}`, {
      headers: { ...getAuthHeaders() },
    });
    if (!response.ok) {
      console.warn("Failed to fetch files, returning empty tree.");
      return [];
    }
    const text = await response.text();
    if (!text) return [];
    try {
      const data = JSON.parse(text);
      const filesData = Array.isArray(data)
        ? data
        : (data.files || data.content || data.root || data.data || []);
      if (!Array.isArray(filesData)) return [];
      return buildFileTree(filesData);
    } catch (e) {
      console.warn("Failed to parse files JSON, returning empty tree.");
      return [];
    }
  },

  async getFileContent(projectId: string, path: string): Promise<string> {
    const pathParts = path.split("/").map(encodeURIComponent).join("/");
    const response = await fetch(
      `${BASE_URL}/api/v1/workspace/projects/${projectId}/files/${pathParts}?_t=${Date.now()}`,
      { headers: { ...getAuthHeaders() } }
    );
    if (!response.ok) {
      console.error(`Error fetching file: ${response.status} ${response.statusText}`);
      throw new Error("Failed to fetch file content");
    }
    const text = await response.text();
    if (!text) return "";
    try {
      const data = JSON.parse(text);
      return data.content !== undefined ? data.content : text;
    } catch (e) {
      return text;
    }
  },

  async downloadProjectZip(id: string): Promise<Blob> {
    const response = await fetch(`${BASE_URL}/api/v1/workspace/projects/${id}/files/download-zip`, {
      headers: { ...getAuthHeaders() },
    });
    if (!response.ok) throw new Error("Failed to download project");
    return response.blob();
  },

  // ─── Workspace: Project Members ───────────────────────────────────────────────

  async getProjectMembers(projectId: string): Promise<ProjectMember[]> {
    const response = await fetch(`${BASE_URL}/api/v1/workspace/projects/${projectId}/members`, {
      headers: { ...getAuthHeaders() },
    });
    if (!response.ok) throw new Error("Failed to fetch project members");

    const text = await response.text();
    if (!text) return [];

    try {
      const members: any[] = JSON.parse(text);
      if (!Array.isArray(members)) return [];

      return members.map(m => ({
        userId: m.userId || m.id,
        username: m.email || m.username || "",
        name: m.name || "",
        role: m.projectRole || m.role || 'VIEWER',
        invitedAt: m.invitedAt,
      }));
    } catch (e) {
      console.warn("Failed to parse project members JSON", e);
      return [];
    }
  },

  async inviteMember(projectId: string, username: string, role: ProjectRole): Promise<void> {
    const response = await fetch(`${BASE_URL}/api/v1/workspace/projects/${projectId}/members`, {
      method: "POST",
      headers: { "Content-Type": "application/json", ...getAuthHeaders() },
      body: JSON.stringify({ username, role }),
    });
    if (!response.ok) {
      const error = await response.text();
      throw new Error(error || "Failed to invite member");
    }
  },

  async updateMemberRole(projectId: string, userId: number, role: ProjectRole): Promise<void> {
    const response = await fetch(`${BASE_URL}/api/v1/workspace/projects/${projectId}/members/${userId}`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json", ...getAuthHeaders() },
      body: JSON.stringify({ role }),
    });
    if (!response.ok) throw new Error("Failed to update member role");
  },

  async removeMember(projectId: string, userId: number): Promise<void> {
    const response = await fetch(`${BASE_URL}/api/v1/workspace/projects/${projectId}/members/${userId}`, {
      method: "DELETE",
      headers: { ...getAuthHeaders() },
    });
    if (!response.ok) throw new Error("Failed to remove member");
  },

  // ─── Workspace: Deploy & Preview ─────────────────────────────────────────────

  async deploy(projectId: string): Promise<DeployResponse> {
    const response = await fetch(`${BASE_URL}/api/v1/workspace/projects/${projectId}/deploy`, {
      method: "POST",
      headers: { ...getAuthHeaders() },
    });
    if (!response.ok) throw new Error("Deployment failed");
    return response.json();
  },

  // ─── Intelligence: Chat ───────────────────────────────────────────────────────


  async getChatHistory(projectId: string): Promise<ChatMessage[]> {
    const numericProjectId = Number(projectId);
    if (!projectId || projectId === "undefined" || isNaN(numericProjectId)) {
      console.warn("Invalid projectId passed to getChatHistory", projectId);
      return [];
    }

    try {
      let response = await fetch(`${BASE_URL}/api/v1/intelligence/chat/projects/${numericProjectId}/chat?_t=${Date.now()}`, {
        headers: { ...getAuthHeaders() },
      });

      if (!response.ok && response.status === 404) {
        response = await fetch(`${BASE_URL}/api/v1/intelligence/chat/projects/${numericProjectId}/chat?_t=${Date.now()}`, {
          headers: { ...getAuthHeaders() },
        });
      }

      if (!response.ok) {
        console.warn("Failed to fetch chat history, returning empty history.");
        return [];
      }

      const text = await response.text();
      if (!text) return [];

      const data = JSON.parse(text);
      console.log("RAW chat history data from backend:", data);

      let rawMessages = [];
      if (Array.isArray(data)) {
        rawMessages = data;
      } else {
        rawMessages = data.messages || data.content || data.data || data.response || data.chatMessages || [];
        if (!rawMessages.length && data._embedded) {
          const keys = Object.keys(data._embedded);
          if (keys.length > 0) rawMessages = data._embedded[keys[0]];
        }
      }

      // Safeguard structure mapping to structural types expected by ChatPanel.tsx
      return rawMessages.map((msg: any): ChatMessage => {
        // Convert USER/ASSISTANT to lowercase strings to align with component templates
        const rawRole = String(msg.role || "assistant").toLowerCase();
        const cleanRole = rawRole === "user" ? "user" : "assistant";

        // Extract and normalize potential database sub-events
        const parsedEvents = Array.isArray(msg.events)
          ? msg.events.map((ev: any) => ({
            id: ev.id,
            type: ev.type,
            content: ev.content || "",
            metadata: ev.metadata || ev.metaData || "",
            filePath: ev.filePath || "",
            sequenceOrder: ev.sequenceOrder || 0
          }))
          : [];

        return {
          id: msg.id?.toString() || Math.random().toString(),
          role: cleanRole,
          content: msg.content || "",
          createdAt: msg.createdAt || new Date().toISOString(),
          events: parsedEvents,
          isStreaming: false
        };
      });

    } catch (e) {
      console.error("Failed to parse chat history JSON:", e);
      return [];
    }
  },

  async streamChat(
    projectId: string,
    message: string,
    onChunk: (chunk: string) => void,
    onFile: (path: string, content: string) => void,
    onComplete: () => void,
    onError: (error: Error) => void
  ) {
    const controller = new AbortController();

    fetch(`${BASE_URL}/api/v1/intelligence/chat/stream`, {
      method: "POST",
      headers: { "Content-Type": "application/json", ...getAuthHeaders() },
      body: JSON.stringify({ message, projectId: Number(projectId) }),
      signal: controller.signal,
    })
      .then(async (response) => {
        if (!response.ok) throw new Error("Chat stream failed");

        const reader = response.body?.getReader();
        if (!reader) throw new Error("No reader available");

        const decoder = new TextDecoder();
        let sseBuffer = "";

        while (true) {
          const { done, value } = await reader.read();
          if (done) break;

          sseBuffer += decoder.decode(value, { stream: true });

          const lines = sseBuffer.split("\n");
          sseBuffer = lines.pop() || "";

          for (const line of lines) {
            const trimmedLine = line.trim();
            if (!trimmedLine || !trimmedLine.startsWith("data:")) continue;

            const dataStr = trimmedLine.slice(5).trim();
            if (!dataStr) continue;

            let content: string;
            try {
              const parsed = JSON.parse(dataStr);
              content = parsed.text !== undefined ? parsed.text : dataStr;
            } catch (e) {
              content = dataStr;
            }

            if (content.includes("StreamResponse[text=")) {
              content = content
                .split("StreamResponse[text=")
                .filter(Boolean)
                .map(part => {
                  const lastIdx = part.lastIndexOf("]");
                  return lastIdx !== -1
                    ? part.slice(0, lastIdx) + part.slice(lastIdx + 1)
                    : part;
                })
                .join("");
            }

            onChunk(content);
          }
        }
        onComplete();
      })
      .catch((error) => {
        if (error.name !== "AbortError") {
          console.error("Stream error:", error);
          onError(error);
        }
      });

    return () => controller.abort();
  },
};