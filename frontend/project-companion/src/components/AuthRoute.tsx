import { Navigate, Outlet } from "react-router-dom";
import { isAuthenticated } from "@/lib/api";

export function AuthRoute() {
  const isAuth = isAuthenticated();

  if (isAuth) {
    // If user is already logged in, redirect them to the projects dashboard
    return <Navigate to="/projects" replace />;
  }

  return <Outlet />;
}
