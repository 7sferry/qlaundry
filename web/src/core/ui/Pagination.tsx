/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

import {ChevronLeft, ChevronRight} from 'lucide-react';
import {Button} from './Button';

interface PaginationProps {
	hasNext: boolean;
	hasPrev: boolean;
	onNext: () => void;
	onPrev: () => void;
	loading?: boolean;
}

export function Pagination({hasNext, hasPrev, onNext, onPrev, loading}: PaginationProps) {
	return (
			<div className="row" style={{gap: 8, justifyContent: 'flex-end', marginTop: 16}}>
				<Button type="button" variant="ghost" disabled={!hasPrev || loading} onClick={onPrev}>
					<ChevronLeft size={15}/> Previous
				</Button>
				<Button type="button" variant="ghost" disabled={!hasNext || loading} onClick={onNext}>
					Next <ChevronRight size={15}/>
				</Button>
			</div>
	);
}
