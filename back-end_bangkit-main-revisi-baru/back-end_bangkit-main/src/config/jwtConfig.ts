import dotenv from "dotenv";

dotenv.config();

export const jwtSecret: any = process.env.jwt_secret; // Pastikan kunci ini disimpan aman di file .env pada produksi
export const jwtExpiresIn = "1h"; // Waktu kadaluwarsa token
