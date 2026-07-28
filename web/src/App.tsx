/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {BrowserRouter, Navigate, Outlet, Route, Routes} from 'react-router-dom';
import ThemeProvider from '@/core/theme/ThemeProvider';
import {Loading, ToastProvider} from '@/core/ui';
import {useAuth} from '@/features/auth/presentation/useAuth';
import LoginPage from '@/features/auth/presentation/pages/LoginPage';
import RegisterPage from '@/features/auth/presentation/pages/RegisterPage';
import ForgotPasswordPage from '@/features/auth/presentation/pages/ForgotPasswordPage';
import DashboardPage from '@/features/dashboard/presentation/pages/DashboardPage';
import CreateOrderPage from '@/features/orders/presentation/pages/CreateOrderPage';
import OrderHistoryPage from '@/features/orders/presentation/pages/OrderHistoryPage';
import ReportsPage from '@/features/reports/presentation/pages/ReportsPage';
import CustomersPage from '@/features/customers/presentation/pages/CustomersPage';
import StaffPage from '@/features/staff/presentation/pages/StaffPage';
import StaffSettingsPage from '@/features/staff/presentation/pages/StaffSettingsPage';
import Sidebar from '@/shared/components/Sidebar';
import Topbar from '@/shared/components/Topbar';
import {AuthProvider} from "@/features/auth/presentation/AuthContextProvider";

function ProtectedLayout() {
  const {isAuthenticated, isLoading} = useAuth();

  if (isLoading) {
    return (
        <div style={{height: '100vh', display: 'grid', placeItems: 'center'}}>
          <Loading label="Loading…"/>
        </div>
    );
  }

  if (!isAuthenticated) return <Navigate to="/login" replace/>;

  return (
      <div className="app-shell">
        <Sidebar/>
        <main className="app-main">
          <Topbar/>
          <div className="app-content">
            <Outlet/>
          </div>
        </main>
      </div>
  );
}

function AuthLayout() {
  const {isAuthenticated, isLoading} = useAuth();

  if (isLoading) {
    return (
        <div style={{height: '100vh', display: 'grid', placeItems: 'center'}}>
          <Loading label="Loading…"/>
        </div>
    );
  }

  if (isAuthenticated) return <Navigate to="/dashboard" replace/>;

  return <Outlet/>;
}

export default function App() {
  return (
      <ThemeProvider>
        <ToastProvider>
          <AuthProvider>
            <BrowserRouter>
              <Routes>
                <Route element={<AuthLayout/>}>
                  <Route path="/login" element={<LoginPage/>}/>
                  <Route path="/register" element={<RegisterPage/>}/>
                  <Route path="/forgot-password" element={<ForgotPasswordPage/>}/>
                </Route>

                <Route element={<ProtectedLayout/>}>
                  <Route index element={<Navigate to="/dashboard" replace/>}/>
                  <Route path="/dashboard" element={<DashboardPage/>}/>
                  <Route path="/orders/new" element={<CreateOrderPage/>}/>
                  <Route path="/orders/history" element={<OrderHistoryPage/>}/>
                  <Route path="/customers" element={<CustomersPage/>}/>
                  <Route path="/staff" element={<StaffPage/>}/>
                  <Route path="/settings" element={<StaffSettingsPage/>}/>
                  <Route path="/reports" element={<ReportsPage/>}/>
                  <Route path="*" element={<Navigate to="/dashboard" replace/>}/>
                </Route>
              </Routes>
            </BrowserRouter>
          </AuthProvider>
        </ToastProvider>
      </ThemeProvider>
  );
}
