import { describe, expect, it, beforeEach, vi } from 'vitest';
import { AuthService, UserResponse } from './auth.service';

function setupStorage() {
  const store: Record<string, string> = {};
  const storage = {
    getItem: vi.fn((key: string) => (key in store ? store[key] : null)),
    setItem: vi.fn((key: string, value: string) => {
      store[key] = value;
    }),
    removeItem: vi.fn((key: string) => {
      delete store[key];
    })
  };

  Object.defineProperty(globalThis, 'localStorage', {
    value: storage,
    configurable: true
  });

  Object.defineProperty(globalThis, 'window', {
    value: { localStorage: storage },
    configurable: true
  });

  return { store, storage };
}

describe('AuthService', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
  });

  it('isAuthenticated should return true when token exists', () => {
    setupStorage();
    const router = { navigate: vi.fn() } as any;
    const http = { post: vi.fn() } as any;
    const service = new AuthService(http, router);

    service.setToken('abc-token');

    expect(service.isAuthenticated()).toBe(true);
    expect(service.getToken()).toBe('abc-token');
  });

  it('updateStoredUser should merge patch into existing user', () => {
    const { store } = setupStorage();
    const router = { navigate: vi.fn() } as any;
    const http = { post: vi.fn() } as any;
    const service = new AuthService(http, router);

    const user: UserResponse = { id: 1, username: 'old-name', email: 'a@b.com' };
    store.user = JSON.stringify(user);

    service.updateStoredUser({ username: 'new-name' });

    const updated = service.getUser();
    expect(updated?.username).toBe('new-name');
    expect(updated?.email).toBe('a@b.com');
  });

  it('logout should clear storage and navigate to home', () => {
    const { store, storage } = setupStorage();
    store.token = 'abc';
    store.user = '{"id":1}';

    const router = { navigate: vi.fn() } as any;
    const http = { post: vi.fn() } as any;
    const service = new AuthService(http, router);

    service.logout();

    expect(storage.removeItem).toHaveBeenCalledWith('token');
    expect(storage.removeItem).toHaveBeenCalledWith('user');
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });
});
