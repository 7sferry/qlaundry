/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import type {FC} from 'react';
import {useLocation} from 'react-router-dom';
import {CheckCircle2} from 'lucide-react';

const PATH_TITLES: Record<string, string> = {
	'/dashboard': 'Dashboard',
	'/orders/new': 'Create new order',
	'/orders/history': 'Order history',
	'/customers': 'Customer management',
	'/staff': 'Staff management',
	'/settings': 'Account settings',
	'/reports': 'Reports & analytics',
};

const DATE_STR = new Date().toLocaleDateString('en-GB', {
	weekday: 'long',
	day: 'numeric',
	month: 'long',
	year: 'numeric',
});

const Topbar: FC = () => {
	const {pathname} = useLocation();
	const title = PATH_TITLES[pathname] ?? 'QLaundry';

	return (
			<div className="topbar">
				<div className="topbar__title">
					<small>{DATE_STR}</small>
					<strong>{title}</strong>
				</div>
				<div className="topbar__status">
					<CheckCircle2 size={14} style={{color: 'var(--success)'}}/>
					System operating normally
				</div>
			</div>
	);
};

export default Topbar;
