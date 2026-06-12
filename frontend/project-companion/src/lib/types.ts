export interface LoginCredentials {
  username: string;
  password: string;
}

export interface UserProfileResponse {
  id: number;
  name: string;
  username: string;
}

export interface LoginResponse {
  token: string;
  projectId: string; // Left this here in case frontend uses it
  userProfileResponse?: UserProfileResponse;
}

export interface FileNode {
  name?: string; // Optional since API only returns path, frontend populates name
  path: string;
  type?: "file" | "directory";
  children?: FileNode[];
}

export interface DeployResponse {
  previewUrl: string;
}

export interface ChatHistoryMessage {
  id: number;
  role: "USER" | "ASSISTANT";
  content: string;
  createdAt: string;
}

export enum ChatEventType {
  THOUGHT = 'THOUGHT',
  MESSAGE = 'MESSAGE',
  FILE_EDIT = 'FILE_EDIT',
  TOOL_LOG = 'TOOL_LOG'
}

export interface ChatEvent {
  id?: number;
  type: ChatEventType;
  content: string;
  metadata?: string;
  metaData?: string;
  filePath?: string;
  sequenceOrder?: number;
}

export interface ChatMessage {
  id: string; // Keep string to easily handle combined frontend temporary IDs + database long IDs
  role: "user" | "assistant";
  content: string;
  isStreaming?: boolean;
  createdAt?: string;
  events?: ChatEvent[];
  editedFiles?: string[];
}
export interface ProjectSummaryResponse {
  id: number;
  name: string;
  createdAt: string;
  updatedAt?: string;
  description?: string;
  thumbnailUrl?: string; // Optional URL for project thumbnail
  role?: ProjectRole; // Added to show user's role in the project list
}

export interface ProjectResponse {
  id: number;
  name: string;
  role?: ProjectRole; // Added to check user's permission in the project
  createdAt: string;
  updatedAt?: string;
  owner?: UserProfileResponse;
}

export interface ProjectRequest {
  name: string;
}

export type ProjectRole = 'OWNER' | 'EDITOR' | 'VIEWER';

export interface ProjectMember {
  userId: number;
  username: string; // mapped from email
  name?: string;
  role: ProjectRole; // mapped from projectRole
  invitedAt?: string;
}

export interface MemberResponse {
  userId: number;
  name?: string;
  email: string;
  projectRole: ProjectRole;
  invitedAt?: string;
}

export interface InvitedMemberRequest {
  username: string;
  role: ProjectRole;
}

export interface UpdateMemberRole {
  role: ProjectRole;
}

export interface SignupRequest {
  username: string;
  name: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  userProfileResponse: UserProfileResponse;
}

// New Types from OpenAPI
export interface PortalResponse {
  portalUrl: string;
}

export interface CheckoutRequest {
  planId: number;
  successUrl?: string;
  cancelUrl?: string;
}

export interface CheckoutResponse {
  checkoutUrl: string;
}

export interface UsageTodayResponse {
  tokensUsed: number;
  tokensLimit: number;
  previewsRunning: number;
  previewsLimit: number;
}

export interface PlanLimitsResponse {
  planName: string;
  maxTokenPerDay: number;
  maxProjects: number;
  unlimitedAi: boolean;
}

export interface PlanResponse {
  id: number;
  name: string;
  maxProducts: number;
  maxTokenPerDay: number;
  unlimitedAi: boolean;
  price: string;
}

export interface SubscriptionResponse {
  plan: PlanResponse;
  status: string;
  currentPeriodEnd: string;
  tokenUsedThisCycle: number;
}