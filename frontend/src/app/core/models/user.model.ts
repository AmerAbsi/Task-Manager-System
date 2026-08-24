import { Role } from './enums';

export interface User {
  id: number;
  username: string;
  email: string;
  fullName: string;
  role: Role;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}
export interface UserRequest {
  username: string;
  password: string;
  email: string;
  fullName: string;
  role: Role;
  active: boolean;
}

export interface UserUpdateRequest {
  email: string;
  fullName: string;
  role: Role;
  active: boolean;
}

export interface ProfileUpdateRequest {
  email: string;
  fullName: string;
  password?: string;
  currentPassword?: string;
}