package com.teamtreehouse.contactmgr;

import com.teamtreehouse.contactmgr.model.Contact;
import com.teamtreehouse.contactmgr.model.Contact.ContactBuilder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * Created by Hydra1 on 14-11-16.
 */
public class Application {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory(){
        // Create a StandardServiceRegistry
        final ServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        return new MetadataSources(registry).buildMetadata().buildSessionFactory();

    }


    public static void main(String[] args) {
        // Hold a reusable reference to a SessionFactory(since we only need one)
        Contact contact = new ContactBuilder("Chris", "Ramacciotti")
                .withEmail("blabla@email.com")
                .withPhone(35235252L)
                .build();
        int id = save(contact);

        // Display a list of contacts before the update
        System.out.println("\n\nBefore update\n\n");
        fetchAllContacts().stream().forEach(System.out::println);

        // Get the persisted contact
        Contact c = findContactById(id);
        // Update the contact
        System.out.println("\n\nUpdating...\n\n");
        c.setFirstName("Maarten");
        // Persist the changes
        System.out.println("\n\nUpdate complete!\n\n");
        update(c);
        // Display a list of contacts after the update
        System.out.println("\n\nAfter update\n\n");
        fetchAllContacts().stream().forEach(System.out::println);
        // Delete the contact
        Contact three = findContactById(3);
        System.out.println("\n\nDeleting ...\n\n");
        delete(three);
        fetchAllContacts().stream().forEach(System.out::println);
    }

    private static Contact findContactById(int id){
        // Open a session
        Session session = sessionFactory.openSession();
        // Retrieve a persistent object (or null if not found)
        Contact contact = session.get(Contact.class, id);
        // Close the session
        session.close();
        // Return the object
        return contact;
    }

    private static void delete(Contact contact){
        // Open a session
        Session session = sessionFactory.openSession();

        // Begin a transaction
        session.beginTransaction();

        // Use sessesion to delete contact
        session.delete(contact);

        // Commit the transaction
        session.getTransaction().commit();

        //Close the session
        session.close();
    }

    private static void update(Contact contact){
        // Open a session
        Session session = sessionFactory.openSession();
        // Begin a transaction
        session.beginTransaction();
        // Use the session to update the contact
        session.update(contact);
        // Commit the transaction
        session.getTransaction().commit();
        // Close the session
        session.close();
    }

    @SuppressWarnings("unchecked")
    private static List<Contact> fetchAllContacts(){
        // Open a session
        Session session = sessionFactory.openSession();

        // @ DEPRECATED Create Criteria Object
        //Criteria criteria = session.createCriteria(Contact.class);

        // @ DEPRECATED Get a list of contact objects according to the criteria object
        //List<Contact> contacts = criteria.list();


        // Updated: Create CriteriaBuilder
        CriteriaBuilder builder = session.getCriteriaBuilder();

        // Updated: Create CriteriaQuery
        CriteriaQuery<Contact> criteria = builder.createQuery(Contact.class);

        // Updated: Specifiy criteria root
        criteria.from(Contact.class);

        // Updated: Execute query
        List<Contact> contacts = session.createQuery(criteria).getResultList();

        // Close a session
        session.close();
        return contacts;
    }

    private static int save(Contact contact){
        // Open a session
        Session session = sessionFactory.openSession();

        // Begin a transaction
        session.beginTransaction();

        // Use the session to save the contact
        int id = (int)session.save(contact);

        // Commit the transaction
        session.getTransaction().commit();

        // Close the session
        session.close();

        return id;
    }
}
