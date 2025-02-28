# Consumer Complaint Classification Model Task 5 

## Overview  
This task is to build a **machine learning model** that classifies consumer complaints into predefined categories based on the complaint text. The model is trained using **XGBoost** and **TF-IDF vectorization**, achieving high accuracy in text classification.

## Programming Language Used  
- **Python** (`.py` script)

## Model Workflow  
1. **Data Loading**: The dataset (`complaints.csv`) is loaded and filtered to keep only relevant categories.
2. **Preprocessing**:
   - Removing null values
   - Text cleaning (lowercasing, punctuation removal, stopword filtering)
   - Mapping complaint categories to numerical labels (`0,1,2,3`)
3. **Feature Extraction**:
   - Using **TF-IDF vectorization** to convert text into numerical form.
4. **Model Training**:
   - **Algorithm Used**: XGBoost Classifier
      `n_estimators=100`, `max_depth=6`, `learning_rate=0.1`
5. **Model Evaluation**:
   - Accuracy, Precision, Recall, F1-score
   - **Confusion Matrix** for visualizing classification performance
   - **Error Distribution Plot** to analyze misclassifications
6. **Prediction**:
   - Takes user input complaint text and predicts the corresponding category.

##  Model Results  

#### Classification Report  
- **Overall Accuracy**: `95.7%`  
- **Precision, Recall, F1-score**:  

![results](SCREENSHOTS/results.png) 

### Confusion Matrix  
Shows actual vs. predicted categories.  

![Confusion](SCREENSHOTS/confusion.png)

###  Error Distribution Plot  
Analyzes misclassified complaints.  
![Error](SCREENSHOTS/error.png)

---

## How to Run the Model  
```bash
python model.py
```
## Conclusion  

> This project successfully implements a text classification system that accurately categorizes consumer complaints into four major categories. With an accuracy of 95.7%, it provides a solid baseline for automating complaint classification.

> By reducing manual effort in customer support, this model could be highly beneficial for financial institutions, consumer protection agencies, and customer service teams.
