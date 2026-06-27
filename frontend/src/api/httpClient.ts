import type { ApiResponse } from '@/types/myWork';

export async function getApi<T>(path: string, params: Record<string, string | number | undefined> = {}): Promise<T> {
  const searchParams = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined) {
      searchParams.set(key, String(value));
    }
  });

  const query = searchParams.toString();
  const response = await fetch(`${path}${query ? `?${query}` : ''}`, {
    headers: {
      'X-User-Id': '1'
    }
  });

  if (!response.ok) {
    throw new Error(`API 요청 실패 (${response.status})`);
  }

  const body = (await response.json()) as ApiResponse<T>;
  return body.data;
}
