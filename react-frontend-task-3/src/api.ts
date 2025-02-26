import axios from "axios";

const API_URL = "http://localhost:8080/tasks"; // Make sure this is correct

// Fetch all tasks
export const getTasks = () => axios.get(API_URL);

// Search tasks by name
export const searchTasks = (name: string) => axios.get(`${API_URL}/search?name=${name}`);

// Create a new task
export const createTask = async (task: any) => {
    console.log("Sending API Request:", task);
    return axios.post(API_URL, task);
};

// Delete a task
export const deleteTask = (id: string) => axios.delete(`${API_URL}/${id}`);
