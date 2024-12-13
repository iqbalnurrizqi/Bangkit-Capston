import { Router } from "express";
import { authenticateToken } from "../middleware/authMiddleware";
import { routineController, routineDetailController } from "../controller/routine.controller";

const router = Router();

// Get Routines
router.get("/", authenticateToken, routineController );


// Add Product to Routine
router.post("/", authenticateToken, routineDetailController );



export default router;
