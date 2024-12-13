import { Router } from "express";
import { authenticateToken } from "../middleware/authMiddleware";
import { homeController } from "../controller/home.controller";

const router = Router();

router.get("/", authenticateToken, homeController );

export default router;
