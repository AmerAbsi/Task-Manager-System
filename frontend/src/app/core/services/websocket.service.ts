import { Injectable, signal } from '@angular/core';
import { Client, IMessage } from '@stomp/stompjs';
import { environment } from '../../../environments/environment';
import { AppNotification } from '../models/notification.model';

@Injectable({ providedIn: 'root' })
export class WebSocketService {

  private client: Client | null = null;

  connected = signal(false);
  latestNotification = signal<AppNotification | null>(null);

  connect(token: string) {
    if (!token || this.client?.active) {
      return;
    }

    this.client = new Client({
      brokerURL: environment.wsUrl,
      connectHeaders: {
        Authorization: `Bearer ${token}`
      },
      reconnectDelay: 5000,
      onConnect: () => {
        this.connected.set(true);
        this.subscribeToNotifications();
      },
      onDisconnect: () => this.connected.set(false),
      onStompError: () => this.connected.set(false)
    });

    this.client.activate();
  }

  disconnect() {
    this.client?.deactivate();
    this.client = null;
    this.connected.set(false);
  }

  private subscribeToNotifications() {
    this.client?.subscribe('/user/queue/notifications', (message: IMessage) => {
      try {
        const notification: AppNotification = JSON.parse(message.body);
        this.latestNotification.set(notification);
      } catch {
        // ignore malformed payloads
      }
    });
  }
}