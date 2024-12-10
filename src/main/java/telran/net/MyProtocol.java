package telran.net;

public class MyProtocol implements Protocol {

    @Override
    public Response getResponse(Request request) {
        // Извлечение данных из request напрямую через поля
        String requestType = request.requestType();
        String requestData = request.requestData();

        // Логика обработки запроса в зависимости от типа запроса
        switch (requestType) {
            case "exampleType1":
                // Обработка типа запроса "exampleType1"
                return new Response(ResponseCode.OK, "Response data for exampleType1");
            case "exampleType2":
                // Обработка типа запроса "exampleType2"
                return new Response(ResponseCode.OK, "Response data for exampleType2");
            default:
                return new Response(ResponseCode.WRONG_TYPE, "Unsupported request type");
        }
    }
}
