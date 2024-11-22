import OurTable from "main/components/OurTable";

const columns = [
  {
    Header: "id",
    accessor: "id", // accessor is the "key" in the data
  },
  {
    Header: "Requester Email",
    accessor: "requesterEmail",
  },
  {
    Header: "Requester Name",
    accessor: "requesterName",
  },
  {
    Header: "Professor Email",
    accessor: "professorEmail",
  },
  {
    Header: "Professor Name",
    accessor: "professorName",
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

export default function RequestsTable({ requests }) {
  return (
    <OurTable data={requests} columns={columns} testid={"RequestsTable"} />
  );
}
