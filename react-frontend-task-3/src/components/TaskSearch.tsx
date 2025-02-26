import React, { useState } from "react";
import { Input, Button, message } from "antd";
import { searchTasks } from "../api";

interface Props {
  setTasks: (tasks: any[]) => void;
}

const TaskSearch: React.FC<Props> = ({ setTasks }) => {
  const [name, setName] = useState("");

  const handleSearch = async () => {
    try {
      const { data } = await searchTasks(name);
      setTasks(data);
    } catch (error) {
      message.error("Search failed.");
    }
  };

  return (
    <div style={{ display: "flex", gap: "10px", marginBottom: "20px" }}>
      <Input value={name} onChange={(e) => setName(e.target.value)} placeholder="Search by name" />
      <Button onClick={handleSearch}>Search</Button>
    </div>
  );
};

export default TaskSearch;
