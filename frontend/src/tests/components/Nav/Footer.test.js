import { render, screen } from "@testing-library/react";
import Footer from "main/components/Nav/Footer";

describe("Footer tests", () => {
  test("renders correctly", async () => {
    render(<Footer />);
    const expectedText =
      "Built by students using React and Spring Boot ❤️ See the source code on Github.";
    expect(screen.getByTestId("Footer").textContent).toBe(expectedText);
  });
});
