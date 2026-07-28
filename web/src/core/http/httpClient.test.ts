import {describe, expect, it, vi} from 'vitest';
import {withFallback} from './httpClient';

describe('withFallback', () => {
	it('returns the live result when the live call succeeds', async () => {
		const live = vi.fn().mockResolvedValue('live data');
		const fallback = vi.fn().mockReturnValue('fallback data');

		const result = await withFallback(live, fallback);

		expect(result).toBe('live data');
		expect(fallback).not.toHaveBeenCalled();
	});

	it('returns the fallback result when the live call throws', async () => {
		const live = vi.fn().mockRejectedValue(new Error('network error'));
		const fallback = vi.fn().mockReturnValue('fallback data');

		const result = await withFallback(live, fallback);

		expect(result).toBe('fallback data');
		expect(fallback).toHaveBeenCalledOnce();
	});

	it('calls onFallback with the thrown error', async () => {
		const error = new Error('timeout');
		const live = vi.fn().mockRejectedValue(error);
		const fallback = vi.fn().mockReturnValue('fallback data');
		const onFallback = vi.fn();

		await withFallback(live, fallback, onFallback);

		expect(onFallback).toHaveBeenCalledOnce();
		expect(onFallback).toHaveBeenCalledWith(error);
	});

	it('does not call onFallback when the live call succeeds', async () => {
		const live = vi.fn().mockResolvedValue('live data');
		const fallback = vi.fn().mockReturnValue('fallback data');
		const onFallback = vi.fn();

		await withFallback(live, fallback, onFallback);

		expect(onFallback).not.toHaveBeenCalled();
	});

	it('supports async fallback functions', async () => {
		const live = vi.fn().mockRejectedValue(new Error('offline'));
		const fallback = vi.fn().mockResolvedValue('async fallback');

		const result = await withFallback(live, fallback);

		expect(result).toBe('async fallback');
	});
});
