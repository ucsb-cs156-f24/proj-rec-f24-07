import OurTable from "main/components/OurTable";

const columns = [
  {
    Header: "id",
    accessor: "id", // accessor is the "key" in the data
  },
  {
    Header: "Requester Id",
    accessor: "requesterId",
  },
  {
    Header: "Professor Id",
    accessor: "professorId",
  },
  {
    Header: "Request Type",
    accessor: "requestType",
  },
  {
    Header: "Details",
    accessor: "details",
  },
  {
    Header: "Needed By Date",
    accessor: "neededByDate",
  },
  {
    Header: "Submission Date",
    accessor: "submissionDate",
  },
  {
    Header: "Completion Date",
    accessor: "completionDate",
  },
  {
    Header: "Status",
    accessor: "status",
  },
];

export default function UsersTable({ requests }) {
  return (
    <OurTable data={requests} columns={columns} testid={"RequestsTable"} />
  );
}
