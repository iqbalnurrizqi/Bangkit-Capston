import * as express from "express";

declare global {
  namespace Express {
    interface Request {
      user?: any; // Anda bisa mengganti `any` dengan tipe yang lebih spesifik, misalnya `UserPayload`
    }
  }
}
