import { Card, CardBody } from "react-bootstrap";

export default function Header({ name, title }) {
  return (
    <div>
      <Card className="my-2 bg-warning" >
        <CardBody>
          <h1 className="text-center my-2">Welcome to Courses Project !</h1>
        </CardBody>
      </Card>
    </div>
  );
}
