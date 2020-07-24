package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import shop.chobitok.modnyi.entity.Client;
import shop.chobitok.modnyi.entity.request.CreateOrderRequest;
import shop.chobitok.modnyi.entity.request.UpdateOrderRequest;
import shop.chobitok.modnyi.novaposta.entity.Data;
import shop.chobitok.modnyi.repository.ClientRepository;

import java.util.List;

@Service
public class ClientService {

    private ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client createClient(CreateOrderRequest createOrderRequest) {
        List<Client> clients = clientRepository.findByPhone(createOrderRequest.getPhone());
        Client client;
        if (clients.size() > 0) {
            client = clients.get(0);
        } else {
            client = new Client();
        }
        client.setPhone(createOrderRequest.getPhone());
        client.setName(createOrderRequest.getName());
        client.setLastName(createOrderRequest.getLastName());
        client.setMiddleName(createOrderRequest.getMiddleName());
        client.setMail(createOrderRequest.getMail());
        client = clientRepository.save(client);
        return client;
    }


    public Client updateOrCreateClient(Client client, UpdateOrderRequest updateOrderRequest) {
        if (client == null) {
            List<Client> clients = clientRepository.findByPhone(updateOrderRequest.getPhone());
            if (clients.size() > 0) {
                client = clients.get(0);
            } else {
                client = new Client();
            }
        }
        client.setName(updateOrderRequest.getName());
        client.setMiddleName(updateOrderRequest.getMiddleName());
        client.setLastName(updateOrderRequest.getLastName());
        client.setPhone(updateOrderRequest.getPhone());
        client.setMail(updateOrderRequest.getMail());
        clientRepository.save(client);
        return client;
    }

    public Client parseClient(Data data) {
        return parseClient(StringUtils.isEmpty(data.getRecipientFullName()) ? data.getRecipientFullNameEW() : data.getRecipientFullName(), data.getPhoneRecipient());
    }

    public Client parseClient(String clientFullName, String phoneRecipient) {
        Client client = null;
        if (!StringUtils.isEmpty(clientFullName)) {
            client = new Client();
            String[] strings = clientFullName.split(" ");
            if (strings.length > 0) {
                client.setLastName(strings[0]);
                client.setName(strings[1]);
                if (strings.length > 2) {
                    client.setMiddleName(strings[2]);
                }
            }
            client.setPhone("+" + phoneRecipient);
        }
        if (!StringUtils.isEmpty(phoneRecipient)) {
            Client fromDb = clientRepository.findOneByPhoneContains(phoneRecipient);
            if (client == null || client.equals(fromDb)) {
                return fromDb;
            }
        }
        return client;
    }

}
