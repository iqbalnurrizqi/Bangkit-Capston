
# C242-PS075 [DeRMA: Dermatology Recognition and Management Aid] - Machine Learning

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

### Workflow to Clone Our Project 
1. Download the dataset from our Google Drive Dataset source
2. Drop the dataset file into your own Google Drive folder (we suggested to create same directory path as in our notebooks)
3. Install all of the library above or in the notebooks using pip into your Google Colab
4. Open our notebook in Google Colab:
   - [SkinCareProductRecommendationSystemModel.ipynb](https://github.com/iqbalnurrizqi/Bangkit-Capstone/blob/Machine-Learning/notebooks/SkinCareRecommendationSystemModel.ipynb)
   - [SkinDiseaseModel.ipynb](https://github.com/iqbalnurrizqi/Bangkit-Capstone/blob/Machine-Learning/notebooks/SkinDiseaseModel.ipynb)
   - [SkinTypeModel.ipynb](https://github.com/iqbalnurrizqi/Bangkit-Capstone/blob/Machine-Learning/notebooks/SkinTypeModel.ipynb)
6. Run the notebook in Google Colab from library import, data preprocessing, until the model save and predict. For the Skin Disease and Skin Type model, we used tensorflow, so we saved it in .h5 format and then deploy it into .tflite format using tfliteconverter. For Skin Care Product Recommendation System model, we used python library, so in deployment stage we used FastAPI to deploy the model.
7. Use the predict cell to predict the image or recommend the product that you want based on the skin type and skin disease in the model
