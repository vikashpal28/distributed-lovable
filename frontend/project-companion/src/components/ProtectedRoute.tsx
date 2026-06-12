import { Navigate, Outlet, useLocation } from "react-router-dom";
import { isAuthenticated } from "@/lib/api";

export function ProtectedRoute() {
  const location = useLocation();
  const isAuth = isAuthenticated();

  if (!isAuth) {
    // Redirect them to the /login page, but save the current location they were
    // trying to go to when they were redirected. This allows us to send them
    // along to that page after they login, which is a nicer user experience.
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return <Outlet />;
}
