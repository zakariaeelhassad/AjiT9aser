export interface ProfileResponse {
    userId: number;
    username: string;
    email: string;
    teamName: string;
    profileImage: string | null;
    teamImage: string | null;
    token?: string | null;
}

export interface ProfileUpdateRequest {
    username?: string;
    email?: string;
    teamName?: string;
    currentPassword?: string;
    newPassword?: string;
    profileImage?: string | null;
    teamImage?: string | null;
}