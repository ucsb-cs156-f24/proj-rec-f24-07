import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import RequestsTable from "main/components/Requests/RequestsTable";

import { useBackend } from "main/utils/useBackend";
const AdminRequestsPage = () => {
  const {
    data: requests,
    error: _error,
    status: _status,
  } = useBackend(
    // Stryker disable next-line all : don't test internal caching of React Query
    ["/api/recommendationrequests/alladmin"],
    { method: "GET", url: "/api/recommendationrequests/alladmin" },
    [],
  );

  return (
    <BasicLayout>
      <h2>Recommendation Requests</h2>
      <RequestsTable requests={requests} />
    </BasicLayout>
  );
};

export default AdminRequestsPage;
