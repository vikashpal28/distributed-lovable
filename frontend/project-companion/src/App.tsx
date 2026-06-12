import { Toaster } from "@/components/ui/toaster";
import { Toaster as Sonner } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Index from "./pages/Index";
import { LoginModal } from "./components/LoginModal";
import { ProjectView } from "./pages/ProjectView";
import { ProjectsDashboard } from "./pages/ProjectsDashboard";
import Signup from "./pages/Signup";
import NotFound from "./pages/NotFound";
import { ProtectedRoute } from "./components/ProtectedRoute";
import { AuthRoute } from "./components/AuthRoute";

const queryClient = new QueryClient();

const App = () => (
  <QueryClientProvider client={queryClient}>
    <TooltipProvider>
      <Toaster />
      <Sonner />
      <BrowserRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
        <Routes>
          {/* Public/Auth Routes - Only accessible when NOT logged in */}
          <Route element={<AuthRoute />}>
            <Route path="/" element={<Index />} />
            <Route path="/login" element={<LoginModal />} />
            <Route path="/signup" element={<Signup />} />
          </Route>

          {/* Protected Routes - Only accessible when logged in */}
          <Route element={<ProtectedRoute />}>
            <Route path="/projects" element={<ProjectsDashboard />} />
            <Route path="/projects/:projectId" element={<ProjectView />} />
            {/* Keeping Index available if it's meant to be a dashboard, otherwise let's just keep /projects */}
            {/* If Index is just a landing page, it should probably be in AuthRoute or public. For now, / redirects to /login if unauth. */}
          </Route>

          {/* Stripe Redirects */}
          <Route path="/frontend/success.html" element={<Navigate to="/projects" replace />} />
          <Route path="/frontend/cancel.html" element={<Navigate to="/projects" replace />} />
          <Route path="/success" element={<Navigate to="/projects" replace />} />
          <Route path="/cancel" element={<Navigate to="/projects" replace />} />
          <Route path="/payment/success" element={<Navigate to="/projects" replace />} />
          <Route path="/payment/cancel" element={<Navigate to="/projects" replace />} />
          <Route path="/payment-success" element={<Navigate to="/projects" replace />} />
          <Route path="/checkout/success" element={<Navigate to="/projects" replace />} />
          <Route path="/api/v1/account/api/payments/success" element={<Navigate to="/projects" replace />} />

          {/* Catch-all */}
          <Route path="*" element={<NotFound />} />
        </Routes>
      </BrowserRouter>
    </TooltipProvider>
  </QueryClientProvider>
);

export default App;
