import { Role } from './enums';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  tokenType: string;
  expiresIn: number;
  userId: number;
  username: string;
  fullName: string;
  role: Role;
}

export interface AuthUser {
  userId: number;
  username: string;
  fullName: string;
  role: Role;
}