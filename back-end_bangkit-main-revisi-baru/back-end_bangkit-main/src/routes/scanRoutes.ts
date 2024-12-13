import { Router } from "express";
import { authenticateToken } from "../middleware/authMiddleware";
import { addScanController, scanController } from "../controller/scan.controller";


const router = Router();

router.get("/history", authenticateToken, scanController);

router.post(
  "/scan-result",
  authenticateToken,
  addScanController
);

export default router;