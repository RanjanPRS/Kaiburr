import React, { useState, useEffect } from "react";
import { Button } from "antd";
import TaskList from "./components/TaskList";
import TaskForm from "./components/TaskForm";
import TaskSearch from "./components/TaskSearch";
import { getTasks } from "./api";

const App: React.FC = () => {
  const [visible, setVisible] = useState(false);
  const [tasks, setTasks] = useState<any[]>([]);

  // Fetch tasks on load
  useEffect(() => {
    loadTasks();
  }, []);

  const loadTasks = async () => {
    try {
      const { data } = await getTasks();
      setTasks(data);
    } catch (error) {
      console.error("Error loading tasks:", error);
    }
  };

  return (
    <div style={{ padding: 20 }}>
      <Button type="primary" onClick={() => setVisible(true)}>New Task</Button>
      <TaskSearch setTasks={setTasks} />
      <TaskList tasks={tasks} loadTasks={loadTasks} />
      <TaskForm visible={visible} onClose={() => setVisible(false)} onSuccess={loadTasks} />
    </div>
  );
};

export default App;
