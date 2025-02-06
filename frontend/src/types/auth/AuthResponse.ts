export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  user: {
    id: string;
    email: string;
    name: string;
    role: string;
  };
}

export interface LoginApiResponse {
  success: boolean;
  data: LoginResponse;
  message?: string;
} 