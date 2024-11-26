import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import { useCurrentUser } from "main/utils/currentUser";

export default function HomePage() {
  const { data: currentUser } = useCurrentUser();

  return (
    <BasicLayout>
      <div className="pt-2">
        <h1>Welcome!</h1>
        <p>
          This is a webapp that allows students to submit different types of
          requests to professors at the University of California, Santa Barbara.
          {currentUser && currentUser.loggedIn ? (
            <span data-testid="home-page-text-navigate">
              {" "}
              Navigate to the requests tab to get started.
            </span>
          ) : (
            <span data-testid="home-page-text-log-in">
              {" "}
              Please log in to get started.
            </span>
          )}
        </p>
      </div>
    </BasicLayout>
  );
}
