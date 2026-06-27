export function formatDate(value: string | null): string {
  if (!value) {
    return '일정 미등록';
  }
  const [, month, day] = value.split('-');
  return `${Number(month)}/${Number(day)}`;
}

export function formatDday(value: number | null): string {
  if (value === null) {
    return '-';
  }
  if (value === 0) {
    return 'D-Day';
  }
  return value < 0 ? `D${value}` : `D+${value}`;
}

export function formatPercent(value: number | null): string {
  return value === null ? '-%' : `${value}%`;
}

export function toDateKey(date: Date): string {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}
