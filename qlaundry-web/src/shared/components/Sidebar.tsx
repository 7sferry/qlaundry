/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {type FC, useEffect, useRef, useState} from 'react';
import {NavLink, useNavigate} from 'react-router-dom';
import {
	BarChart3,
	ClipboardList,
	LayoutDashboard,
	LogOut,
	Moon,
	PackagePlus,
	Settings,
	Sun,
	UserCog,
	Users,
	WashingMachine,
} from 'lucide-react';
import {useAuth} from '@/features/auth/presentation/useAuth';
import {useTheme} from '@/core/theme/useTheme';

const NAV_ITEMS: { path: string; label: string; icon: FC<{ size?: number }> }[] = [
	{path: '/dashboard', label: 'Dashboard', icon: LayoutDashboard},
	{path: '/orders/new', label: 'Order baru', icon: PackagePlus},
	{path: '/orders/history', label: 'Riwayat order', icon: ClipboardList},
	{path: '/customers', label: 'Pelanggan', icon: Users},
	{path: '/staff', label: 'Staf', icon: UserCog},
	{path: '/reports', label: 'Laporan', icon: BarChart3},
];

const Sidebar: FC = () => {
	const {user, logout} = useAuth();
	const {theme, toggleTheme} = useTheme();
	const navigate = useNavigate();
	const [menuOpen, setMenuOpen] = useState(false);
	const menuRef = useRef<HTMLDivElement>(null);

	const initials =
			user?.avatarInitials ??
			(user?.fullName
					? user.fullName.split(' ').slice(0, 2).map((n) => n[0]).join('').toUpperCase()
					: '?');

	useEffect(() => {
		if (!menuOpen) return;
		const handleClickOutside = (e: MouseEvent) => {
			if (menuRef.current && !menuRef.current.contains(e.target as Node)) {
				setMenuOpen(false);
			}
		};
		document.addEventListener('mousedown', handleClickOutside);
		return () => document.removeEventListener('mousedown', handleClickOutside);
	}, [menuOpen]);

	const handleLogout = async () => {
		setMenuOpen(false);
		await logout();
		navigate('/login', {replace: true});
	};

	const goToSettings = () => {
		setMenuOpen(false);
		navigate('/settings');
	};

	return (
			<aside className="sidebar">
				<div className="sidebar__brand">
					<span className="sidebar__logo"><WashingMachine size={20}/></span>
					<div>
						<span className="sidebar__brand-name">QLaundry</span>
						{user?.outletName && (
								<small className="sidebar__outlet">{user.outletName}</small>
						)}
					</div>
				</div>

				<p className="sidebar__eyebrow">Menu</p>

				<nav>
					{NAV_ITEMS.map(({path, label, icon: Icon}) => (
							<NavLink
									key={path}
									to={path}
									className={({isActive}) => `nav-item ${isActive ? 'nav-item--active' : ''}`}
							>
								<Icon size={17}/>
								{label}
							</NavLink>
					))}
				</nav>

				<div className="sidebar__spacer"/>

				<div className="sidebar__footer">
					<button className="nav-item" onClick={toggleTheme}>
						{theme === 'dark' ? <Sun size={17}/> : <Moon size={17}/>}
						{theme === 'dark' ? 'Mode terang' : 'Mode gelap'}
					</button>
					<div className="user-menu-wrap" ref={menuRef}>
						{menuOpen && (
								<div className="user-menu">
									<button className="user-menu__item" onClick={goToSettings}>
										<Settings size={15}/> Pengaturan
									</button>
									<button className="user-menu__item user-menu__item--danger" onClick={() => void handleLogout()}>
										<LogOut size={15}/> Keluar
									</button>
								</div>
						)}
						<button
								className="user-chip user-chip--trigger"
								onClick={() => setMenuOpen((o) => !o)}
								aria-expanded={menuOpen}
						>
							<span className="avatar">{initials}</span>
							<span>
              <strong>{user?.fullName ?? 'User'}</strong>
              <small>{user?.role === 'owner' ? 'Pemilik' : user?.role === 'admin' ? 'Admin' : 'Staff'}</small>
            </span>
						</button>
					</div>
				</div>
			</aside>
	);
};

export default Sidebar;
