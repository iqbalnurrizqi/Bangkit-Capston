import { Request, Response } from "express";
import bcrypt from "bcryptjs";
import jwt from "jsonwebtoken";
import {  auth, db } from "../config/firebaseConfig";
import { jwtSecret, jwtExpiresIn } from "../config/jwtConfig";
import { usersInterface } from "../types/userInterface";
import { loginType } from "../types/loginType";




export const registerAuth = async (req: Request, res: Response) => {
  const { email, password, name }: usersInterface = req.body;

  if (!email || !password || !name) {
    res.status(400).send({
      status: false,
      statusCode: 400,
      message: "Email, password, and name are required",
    });
  }

  try {
    // Periksa apakah pengguna sudah terdaftar
    const userSnapshot = await db
      .collection("users")
      .where("email", "==", email)
      .get();
    if (!userSnapshot.empty) {
      res.status(400).send({
        status: false,
        statusCode: 400,
        message: "Email is already registered",
      });
    }

    // Hash password
    const hashedPassword: string = await bcrypt.hash(password, 10);

    // Simpan pengguna baru ke Firestore
    const newUser: usersInterface = {
      email,
      password: hashedPassword,
      name,
      createdAt: new Date().toISOString(),
    };
    const userRef = await db.collection("users").add(newUser);

    res.status(201).send({
      status: true,
      statusCode: 201,
      message: "User registered successfully",
      userId: userRef.id,
    });
  } catch (error: any) {
    res.status(500).send({
      status: false,
      statusCode: 500,
      message: error.details[0].message,
    });
  }
};

export const loginAuth = async (req: Request, res: Response) => {
  const { email, password }:loginType = req.body;
  if (!email || !password) {
    res.status(400).send({status:false, statusCode:400, message:"Email and password are required"});
  }

  try {
    // Cari pengguna berdasarkan email
    const userSnapshot = await db
      .collection("users")
      .where("email", "==", email)
      .get();
    if (userSnapshot.empty) {
      res.status(401).send({status:false, statusCode:401, message:"Invalid email or password"});
    }

    // Ambil data pengguna
    const userData:any = userSnapshot.docs[0].data();

    // Periksa password
    const isPasswordValid = await bcrypt.compare(password, userData.password);
    if (!isPasswordValid) {
      res.status(401).send({status:false, statusCode:401, message: "Invalid email or password" });
    }

    // Buat token JWT
    const token:string = jwt.sign(
      { id: userSnapshot.docs[0].id, email: userData.email },
      jwtSecret,
      {
        expiresIn: jwtExpiresIn,
      }
    );

    res.status(200).send({status:false, statusCode:200, message: "Login successful", data:token });
  } catch (error: any) {
    res.status(500).send({status:false, statusCode:500, message: error.message });
  }
};

export const forgotPassword = async (req: Request, res: Response) => {
  const { email }:loginType = req.body;

  if (!email) {
    res.status(400).send({status:false, statusCode:400, message: "Email is required" });
  }

  try {
    // Kirim email reset password melalui Firebase Authentication
    await auth.generatePasswordResetLink(email);

    res.status(200).send({
      status: true,
      statusCode: 200,
      message: "Password reset email sent. Please check your inbox.",
    });
  } catch (error: any) {
    if (error.code === "auth/user-not-found") {
      res.status(404).send({status:false, statusCode:404, message: "Email not registered" });
    }

    res
      .status(500)
      .send({status:false, statusCode:500, message: "Something went wrong. Please try again later." });
  }
};