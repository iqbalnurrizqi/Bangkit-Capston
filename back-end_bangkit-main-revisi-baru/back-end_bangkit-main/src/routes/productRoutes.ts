import { Router } from "express";
import { authenticateToken } from "../middleware/authMiddleware";
import { productController, productDetailController } from '../controller/product.controller';

const router = Router();

// Get Product List
router.get("/", productController);

router.get("/:product_id", authenticateToken, productDetailController );

export default router;
