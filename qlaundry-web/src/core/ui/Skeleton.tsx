/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import React from 'react';

interface SkeletonProps {
	width?: string | number;
	height?: string | number;
	radius?: string | number;
	className?: string;
}

export const Skeleton: React.FC<SkeletonProps> = ({
	                                                  width = '100%',
	                                                  height = 14,
	                                                  radius = 6,
	                                                  className = '',
                                                  }) => (
		<div
				className={`skeleton ${className}`.trim()}
				style={{
					width: typeof width === 'number' ? `${width}px` : width,
					height: typeof height === 'number' ? `${height}px` : height,
					borderRadius: typeof radius === 'number' ? `${radius}px` : radius,
				}}
		/>
);

export const SkeletonText: React.FC<{ lines?: number }> = ({lines = 3}) => (
		<div style={{display: 'flex', flexDirection: 'column', gap: 8}}>
			{Array.from({length: lines}, (_, i) => (
					<Skeleton key={i} width={i === lines - 1 ? '60%' : '100%'}/>
			))}
		</div>
);

export const SkeletonCard: React.FC<{ rows?: number }> = ({rows = 3}) => (
		<div className="card" style={{display: 'flex', flexDirection: 'column', gap: 14}}>
			<div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
				<Skeleton width="40%" height={18}/>
				<Skeleton width={60} height={26} radius={99}/>
			</div>
			{Array.from({length: rows}, (_, i) => (
					<Skeleton key={i} height={14} width={i % 2 === 0 ? '90%' : '70%'}/>
			))}
		</div>
);
