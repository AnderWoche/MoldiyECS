package de.moldiy.moldiyecs.componentmanager;

import com.badlogic.gdx.utils.Pool;

import de.moldiy.moldiyecs.utils.Bag;
import de.moldiy.moldiyecs.utils.reflect.ClassReflection;
import de.moldiy.moldiyecs.utils.reflect.ReflectionException;

public class ComponentMapper<T extends Component> {

	private Class<T> componentClass;

	private final Bag<T> components;
	private final Pool<T> componentPool;

	public boolean isLocked = false;
//	private Thread exclusiceAccess = null;
//	private final Lock lock;
//	private final Condition condition;

	private final Bag<ComponentListener> componentListener = new Bag<ComponentMapper.ComponentListener>(
			ComponentListener.class);

	public ComponentMapper(final Class<T> componentClass) {
		this.componentClass = componentClass;
		components = new Bag<T>();
//		this.lock = new ReentrantLock();
//		this.condition = lock.newCondition();
		this.componentPool = new Pool<T>() {
			@Override
			protected T newObject() {
				try {
					return ClassReflection.newInstance(componentClass);
				} catch (ReflectionException e) {
					e.printStackTrace();
				}
				return null;
			}
		};
	}

	/**
	 * Don't forget after the exclusice operation unlock the public access.
	 */
//	public void exclusiceAccess() {
//		if (this.exclusiceAccess == null) {
//			this.exclusiceAccess = Thread.currentThread();
//		} else {
//			this.lock.lock();
//			try {
//				this.condition.await();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			this.lock.unlock();
//			// exclusiceAccess();
//		}
//	}
//
//	public void publicAccess() {
//		this.lock.lock();
//		this.exclusiceAccess = null;
//		this.condition.signalAll();
//		this.lock.unlock();
//	}

	public T get(int entity) {
		if (this.isLocked) {
			synchronized (this) {
				return this.components.safeGet(entity);
			}
		} else {
			return this.components.safeGet(entity);
		}
	}

	/**
	 * GEHÖER NCIHT ZU MEHTODE NUT IDEE NOTOIZ
	 * 
	 * wen erkannt wird das an diesem mapper mehr als 2 Systeme Interesiert sind
	 * wird automatisch sobal die methode REMOVE oder CREATE aufgerufen wird der
	 * mapper nur exclusive nur für diesen thread freigegeben ohne das der benutzer
	 * dieses Frame works was merkt. damit aber am ende der mapper wieder für alle
	 * freigeschaltet wrid wird immer IMMER am system ende automatisch the
	 * PUBLICACCESS methode aufgerufen soweit ich weis goibt es keine exeption wenn
	 * man notifi aufruft obwohl niemand wartet.
	 * 
	 * @param entityID
	 */
	public void remove(int entityID) {
		T component = this.get(entityID);
		if (component != null) {
			/**
			 * Delayed removable Implementation!!!!! JETZT ehmm nö!!.. es egal wann die
			 * removed werden weil ja alles paralel ist Der sugrif auf mapper ist bei
			 * critischen sachen sowieso Sync! LÖSUNG: ein bitVecot der alle enitty markiert
			 * die verändert worden sind und befor ein system anfängt zu arbeiten und die
			 * mapper Synct werden die Subscriptions Aktualieiert mit den BitVEctor!?
			 */
			if (this.isLocked) {
				synchronized (this) {
					this.components.unsafeSet(entityID, null);
					this.componentPool.free(component);
				}
			} else {
				this.components.unsafeSet(entityID, null);
				this.componentPool.free(component);
			}
			this.notifyComponentListener_EntityDeleted(entityID);
		}

	}

	public T create(int entityID) {
		T component = this.get(entityID);
		if (component == null) {
			if(this.isLocked) {
				synchronized (this) {
					component = this.componentPool.obtain();
					this.components.set(entityID, component);
				}
			} else {
				component = this.componentPool.obtain();
				this.components.set(entityID, component);
			}
			this.notifyComponentListener_EntityAdded(entityID);
		}
		return component;
	}

	public void addComponentListener(ComponentListener componentListener) {
		this.componentListener.add(componentListener);
	}

	protected void notifyComponentListener_EntityDeleted(int entity) {
		ComponentListener[] listneners = this.componentListener.getData();
		for (int i = 0, s = this.componentListener.size(); i < s; i++) {
			listneners[i].componentDeleteted(this.componentClass, entity);
		}
	}

	protected void notifyComponentListener_EntityAdded(int entity) {
		ComponentListener[] listneners = this.componentListener.getData();
		for (int i = 0, s = this.componentListener.size(); i < s; i++) {
			listneners[i].componentAdded(this.componentClass, entity);
		}
	}

	public interface ComponentListener {
		public void componentDeleteted(Class<? extends Component> component, int entity);

		public void componentAdded(Class<? extends Component> component, int entity);
	}

}
