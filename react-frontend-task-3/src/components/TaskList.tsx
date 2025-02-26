import React from "react";
import { Table, Button, Collapse } from "antd";
import { deleteTask } from "../api";

interface TaskListProps {
  tasks: any[];
  loadTasks: () => void;
}

const TaskList: React.FC<TaskListProps> = ({ tasks, loadTasks }) => {
  const handleDelete = async (id: string) => {
    try {
      await deleteTask(id);
      loadTasks();
    } catch (error) {
      console.error("Error deleting task:", error);
    }
  };

  return (
    <Table dataSource={tasks} rowKey="id" bordered>
      <Table.Column title="ID" dataIndex="id" key="id" />
      <Table.Column title="Name" dataIndex="name" key="name" />
      <Table.Column title="Owner" dataIndex="owner" key="owner" />
      <Table.Column title="Command" dataIndex="command" key="command" />
      <Table.Column
        title="Actions"
        key="actions"
        render={(_, record) => (
          <Button onClick={() => handleDelete(record.id)} danger>Delete</Button>
        )}
      />
      <Table.Column
        title="Execution History"
        key="executionHistory"
        render={(_, record) => (
          <Collapse>
            {record.taskExecutions && record.taskExecutions.length > 0 ? (
              record.taskExecutions.map((execution: any, index: number) => (
                <Collapse.Panel header={`Execution #${index + 1}`} key={index}>
                  <p><b>Start:</b> {execution.startTime}</p>
                  <p><b>End:</b> {execution.endTime}</p>
                  <p><b>Output:</b> {execution.output}</p>
                </Collapse.Panel>
              ))
            ) : (
              <p>No execution history</p>
            )}
          </Collapse>
        )}
      />
    </Table>
  );
};

export default TaskList;
