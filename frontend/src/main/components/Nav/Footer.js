import { Container } from "react-bootstrap";

export default function Footer() {
  return (
    <footer className="bg-light pt-3 pt-md-4 pb-4 pb-md-5" data-testid="Footer">
      <Container>
        <p>
          Built by students using React and Spring Boot ❤️ See the source code
          on <a href="https://github.com/ucsb-cs156-f24/proj-rec">Github</a>.
        </p>
      </Container>
    </footer>
  );
}
