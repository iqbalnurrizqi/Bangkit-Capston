import { Router } from "express";
import { authenticateToken } from "../middleware/authMiddleware";
import { settingController, updateSettingController } from "../controller/setting.controller";

const router = Router();

// Get Settings
router.get("/", authenticateToken, settingController );

// Update Settings
router.put("/", authenticateToken, updateSettingController);

export default router;
