import { render, screen } from "@testing-library/react";
import HomePage from "main/pages/HomePage";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";

import { apiCurrentUserFixtures } from "fixtures/currentUserFixtures";
import { systemInfoFixtures } from "fixtures/systemInfoFixtures";
import axios from "axios";
import AxiosMockAdapter from "axios-mock-adapter";

describe("HomePage tests", () => {
  const axiosMock = new AxiosMockAdapter(axios);

  beforeEach(() => {
    axiosMock.reset();
    axiosMock.resetHistory();
    axiosMock
      .onGet("/api/systemInfo")
      .reply(200, systemInfoFixtures.showingNeither);
  });

  test("renders correctly for unauthenticated user", async () => {
    const queryClient = new QueryClient();

    axiosMock
      .onGet("/api/currentUser")
      .reply(200, apiCurrentUserFixtures.notLoggedIn);

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <HomePage />
        </MemoryRouter>
      </QueryClientProvider>,
    );

    await screen.findByText(/Welcome!/);
    const logInText = screen.getByTestId("home-page-text-log-in");
    expect(logInText).toBeInTheDocument();
    expect(logInText.textContent).toEqual(" Please log in to get started.");
  });

  test("renders correctly for logged in user", async () => {
    const queryClient = new QueryClient();

    axiosMock
      .onGet("/api/currentUser")
      .reply(200, apiCurrentUserFixtures.userOnly);

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <HomePage />
        </MemoryRouter>
      </QueryClientProvider>,
    );

    await screen.findByText("Log Out");
    const navigateText = screen.getByTestId("home-page-text-navigate");
    expect(navigateText).toBeInTheDocument();
    expect(navigateText.textContent).toEqual(
      " Navigate to the requests tab to get started.",
    );
  });
});
