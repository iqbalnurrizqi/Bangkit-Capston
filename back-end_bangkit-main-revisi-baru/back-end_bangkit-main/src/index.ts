
import express, { Application } from "express";
import bodyParser from "body-parser";
import userRoutes from "./routes/userRoutes";
import authRoutes from "./routes/authRoutes";
import profileRoutes from "./routes/profileRoutes";
import homeRoutes from "./routes/homeRoutes";
import scanRoutes from "./routes/scanRoutes";
import routineRoutes from "./routes/routineRoutes";
import settingRoutes from "./routes/settingRoutes";
import recommendationRoutes from "./routes/recommendationRoutes";
import productRoutes from "./routes/productRoutes";
import cors from "cors";

const app: Application = express();

// Middleware
app.use(cors());
app.use(bodyParser.json());

// Routes
app.use("/user", userRoutes);
app.use("/auth", authRoutes);
app.use("/profile", profileRoutes);
app.use("/home", homeRoutes);
app.use("/scan", scanRoutes);
app.use("/routines", routineRoutes);
app.use("/settings", settingRoutes);
app.use("/recommendations", recommendationRoutes);
app.use("/products", productRoutes);


const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});




