import { describe, expect, it } from 'vitest';

import { formatDate, formatDday, formatPercent, toDateKey } from '@/utils/dateFormatter';

describe('dateFormatter', () => {
  it('formats nullable date values for my-work screens', () => {
    expect(formatDate('2026-03-20')).toBe('3/20');
    expect(formatDate(null)).toBe('일정 미등록');
  });

  it('formats d-day values with the documented sign policy', () => {
    expect(formatDday(-8)).toBe('D-8');
    expect(formatDday(0)).toBe('D-Day');
    expect(formatDday(2)).toBe('D+2');
    expect(formatDday(null)).toBe('-');
  });

  it('formats nullable percent values', () => {
    expect(formatPercent(73)).toBe('73%');
    expect(formatPercent(null)).toBe('-%');
  });

  it('creates stable yyyy-mm-dd keys', () => {
    expect(toDateKey(new Date(2026, 2, 5))).toBe('2026-03-05');
  });
});
