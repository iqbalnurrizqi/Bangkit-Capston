import { Request, Response, NextFunction } from "express";
import jwt from "jsonwebtoken";
import { jwtSecret } from "../config/jwtConfig";

export const authenticateToken = (req: Request, res: Response, next: NextFunction) => {
  const authHeader = req.headers.authorization;
  const token:any = authHeader && authHeader.split(" ")[1];

  if (!token) {
    res.status(401).send({status:false, statusCode:401, message: "Access token is missing" });
  }

  try {
    const decoded = jwt.verify(token, jwtSecret);
    req.user = decoded; // Simpan data pengguna di request
    next();
  } catch (error) {
    res.status(403).send({status:false, statusCode:403, message: "Invalid or expired token" });
  }
};
