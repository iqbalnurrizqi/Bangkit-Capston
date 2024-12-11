# C242-PS075 [DeRMA: Dermatology Recognition and Management Aid] - Machine Learning

<img src="https://github.com/iqbalnurrizqi/Bangkit-Capstone/blob/f1d225724ee9a3b6031678a4d49b39c2b0151140/assets/Github%20Background.png">

### Introduction

The skincare and personal care market in Indonesia is experiencing remarkable growth, especially gen z generations. In 2021, the demand for skincare products in Indonesia surged by 70% compared to the previous year, driven by rising consumer awareness of skin health, and the influence of online sales channels. In 2024, Indonesia’s skincare market is projected to generate US$2.76 billion in revenue, with an annual growth rate (CAGR) of 3.70% from 2024 to 2029.  Despite this growth, many consumers face challenges in selecting suitable products due to limited knowledge about skin health. This statement is supported by evidence based on our idea validation and pre-research on 100 people that are male and female. 

DeRMA is designed to address common skin health challenges by providing personalized guidance on skincare routines and treatments tailored to each user’s unique skin condition.  The personalization features in DeRMA enable users to analyze their skin condition in real-time, receive tailored skincare product recommendations, and access suggestions for trusted and affordable solutions. In addition, providing access to professional consultations for more advanced skin concerns. By including progress tracking and visualization tools, this application not only helps users manage their skin health effectively but also fosters long-term habits for maintaining optimal skin care. It is expected that DeRMA will serve as an effective tool in empowering individuals to achieve healthier, more radiant skin while improving their overall confidence and well-being.  

The machine learning used in this project is using Convolutional Neural Network-based Deep Learning Technology Multi-Label Classification Model as Skin Disease Analyzer and Multi-Class Classification Model as Skin-Type Analyzer with MobileNetV2 Pre-trained model as a backbone and fine tuning it. The third model, that is recommendation system model, we used python to develop it from scratch based on the Skin Disease and Skin Type Analyzer's outputs.

### Dataset
We collect the dataset from Kaggle and Roboflow. Then, we clean the dataset and input it into [DeRMA Dataset](https://drive.google.com/drive/folders/1100X1-tn2Bd4RNML83QXukAO1cFYOc3n?usp=drive_link) so that it can be processed.


### Technologies
Here are the library that used in this project
- kaggle
- roboflow
- beautifulsoup (for skin care product dataset)
-	numpy 
-	pandas 
-	shuutil
- matplotlib.pyplot
- zipfile
- os
- random
- csv
- ipython
- sklearn

You can follow this step to install the library in the google colab with the `requirements.txt` file:
### Project Setup Guide

This guide will help you set up the required environment to run the project. Make sure you have Python installed (preferably version 3.8 or later) and follow the steps below.

#### Installation Steps

1. **Clone the repository**
   ```bash
   !git clone https://github.com/iqbalnurrizqi/Bangkit-Capstone.git
   %cd Bangkit-Capstone
   ```

2. **Switch to the branch with `requirements.txt`**
   ```bash
   !git checkout Machine-Learning
   ```
3. **Install required libraries**
   All required libraries are listed in `requirements.txt`. Use the following command to install them:
   ```bash
   !pip install -r requirements.txt
   ```

4. **Verify the installation**
   To ensure everything is installed correctly, try running the following command:
   ```bash
   !python -c "import tensorflow, sklearn, numpy, matplotlib, pandas, seaborn, kaggle, roboflow, bs4; print('All libraries installed successfully!')"
   ```

### Additional Notes

- **Using Kaggle API**: If you plan to use the Kaggle API to download datasets, make sure you set up your Kaggle API key:
  1. Go to your Kaggle account settings: [https://www.kaggle.com/account](https://www.kaggle.com/account)
  2. Scroll down to the API section and click `Create New API Token`.
  3. Place the downloaded `kaggle.json` file in the `.kaggle` directory in your home folder:
     ```bash
     mkdir ~/.kaggle
     mv /path/to/kaggle.json ~/.kaggle/
     chmod 600 ~/.kaggle/kaggle.json
     ```

- **Using Roboflow**: To use Roboflow's API, ensure you have your API key. Follow the documentation on Roboflow for guidance: [Roboflow API Documentation](https://docs.roboflow.com/)

If you encounter any issues, feel free to open an issue in this repository or reach out to the contributors.

### Workflow to Replicate Our Project 
1. Download the dataset from our Google Drive Dataset source
2. Drop the dataset file into your own Google Drive folder (we suggested to create same directory path as in our notebooks)
3. Install all of the library above or in the notebooks using pip into your Google Colab
4. Open our notebook in Google Colab:
   - [SkinCareProductRecommendationSystemModel.ipynb](https://github.com/iqbalnurrizqi/Bangkit-Capstone/blob/Machine-Learning/notebooks/SkinCareRecommendationSystemModel.ipynb)
   - [SkinDiseaseModel.ipynb](https://github.com/iqbalnurrizqi/Bangkit-Capstone/blob/Machine-Learning/notebooks/SkinDiseaseModel.ipynb)
   - [SkinTypeModel.ipynb](https://github.com/iqbalnurrizqi/Bangkit-Capstone/blob/Machine-Learning/notebooks/SkinTypeModel.ipynb)
6. Run the notebook in Google Colab from library import, data preprocessing, until the model save and predict. For the Skin Disease and Skin Type model, we used tensorflow, so we saved it in .h5 format and then deploy it into .tflite format using tfliteconverter. For Skin Care Product Recommendation System model, we used python library, so in deployment stage we used FastAPI to deploy the model.
7. Use the predict cell to predict the image or recommend the product that you want based on the skin type and skin disease in the model


### Result
__Skin Disease Multilabel Classification Model__ <br>
Training Accuracy-Validation Accuracy <br>
<img src="https://github.com/iqbalnurrizqi/Bangkit-Capstone/blob/8863cf1f3ead26b5a982638138d2ac9cef814f8e/result/Skin%20Disease%20Model%20Result/skindiseasemodel_accuracy%20and%20val_accuracy%20evaluation.jpg"><br>
Training Loss-Validation Loss <br>
<img src="https://github.com/iqbalnurrizqi/Bangkit-Capstone/blob/8863cf1f3ead26b5a982638138d2ac9cef814f8e/result/Skin%20Disease%20Model%20Result/skindiseasemodel_loss%20and%20val_loss%20evaluation.jpg">
<br>

__Skin Type Multiclass Classification Model__ <br>
Training Accuracy-Validation Accuracy <br>
<img src="https://github.com/iqbalnurrizqi/Bangkit-Capstone/blob/8863cf1f3ead26b5a982638138d2ac9cef814f8e/result/Skin%20Type%20Model%20Result/skintypemodel_accuracy%20and%20val_accuracy%20evaluation.jpg"><br>
Training Loss-Validation Loss <br>
<img src="https://github.com/iqbalnurrizqi/Bangkit-Capstone/blob/8863cf1f3ead26b5a982638138d2ac9cef814f8e/result/Skin%20Type%20Model%20Result/skintypemodel_loss%20and%20val_loss%20evaluation.jpg">
