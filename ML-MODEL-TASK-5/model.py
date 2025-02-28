import pandas as pd
import numpy as np
import re
import string
import matplotlib.pyplot as plt
import seaborn as sns

from sklearn.model_selection import train_test_split
from sklearn.feature_extraction.text import TfidfVectorizer, ENGLISH_STOP_WORDS
from xgboost import XGBClassifier
from sklearn.metrics import accuracy_score, classification_report, confusion_matrix, mean_squared_error, r2_score

file_path = "/home/user/Documents/KAIBURR/ML-MODEL-TASK-5/complaints.csv"

try:
    df = pd.read_csv(file_path, low_memory=False)
except Exception as e:
    print(f" Error Loading Dataset: {e}")
    exit()

# Select relevant columns
df = df[["Consumer complaint narrative", "Product"]].dropna()

category_mapping = {
    "Credit reporting, repair, or other": 0,
    "Debt collection": 1,
    "Consumer Loan": 2,
    "Mortgage": 3
}

df = df[df["Product"].isin(category_mapping.keys())]
df["Category"] = df["Product"].map(category_mapping)

df["Category"] = df["Category"] - 1  

# Text Preprocessing Function
def clean_text(text):
    if pd.isnull(text):
        return ""
    text = text.lower()  # Convert to lowercase
    text = re.sub(r'\d+', '', text)  # Remove numbers
    text = text.translate(str.maketrans('', '', string.punctuation))  # Remove punctuation
    words = text.split()  # Tokenization
    words = [word for word in words if word not in ENGLISH_STOP_WORDS]  # Remove stopwords
    return ' '.join(words)

# Apply text cleaning
df["Cleaned_Text"] = df["Consumer complaint narrative"].apply(clean_text)

# Train-Test Split
X_train, X_test, y_train, y_test = train_test_split(
    df["Cleaned_Text"], df["Category"], test_size=0.2, random_state=42)

# Convert text data into numerical vectors - TFIDF
vectorizer = TfidfVectorizer(max_features=5000)
X_train_tfidf = vectorizer.fit_transform(X_train)
X_test_tfidf = vectorizer.transform(X_test)

# Train XGBoost Model
model = XGBClassifier(n_estimators=100, learning_rate=0.1, max_depth=6, use_label_encoder=False, eval_metric='mlogloss')
model.fit(X_train_tfidf, y_train)

# Predictions
y_pred = model.predict(X_test_tfidf)

# Evaluation Metrics
accuracy = accuracy_score(y_test, y_pred)
mse = mean_squared_error(y_test, y_pred)
r2 = r2_score(y_test, y_pred)

print(f"\nModel Accuracy: {accuracy:.4f}")

print("\n📄 Classification Report:")
print(classification_report(y_test, y_pred))

# Confusion Matrixx
cm = confusion_matrix(y_test, y_pred)

plt.figure(figsize=(8, 6))
sns.heatmap(cm, annot=True, fmt="d", cmap="Blues", xticklabels=category_mapping.keys(), yticklabels=category_mapping.keys())
plt.xlabel("Predicted Labels")
plt.ylabel("True Labels")
plt.title("Confusion Matrix")
plt.show()

# Error Distribution plot
errors = y_test - y_pred

plt.figure(figsize=(8, 5))
sns.histplot(errors, bins=30, kde=True)
plt.xlabel("Prediction Error")
plt.ylabel("Count")
plt.title("Error Distribution")
plt.show()

# Function to Predict Category
def predict_category(complaint_text):
    cleaned_text = clean_text(complaint_text)
    vectorized_text = vectorizer.transform([cleaned_text])
    prediction = model.predict(vectorized_text)[0]
    category_label = list(category_mapping.keys())[list(category_mapping.values()).index(prediction + 1)]  # Shift back
    return category_label

# Example Prediction
sample_text = "I am facing issues with my mortgage loan."
predicted_category = predict_category(sample_text)
print(f"\nSample Complaint: {sample_text}")
print(f" Predicted Category: {predicted_category}")