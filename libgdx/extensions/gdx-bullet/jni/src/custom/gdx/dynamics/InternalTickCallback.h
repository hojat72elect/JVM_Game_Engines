

#ifndef InternalTickCallback_H
#define InternalTickCallback_H

#include "../../../bullet/BulletDynamics/Dynamics/btDynamicsWorld.h"

static void InternalTickCallback_CB(btDynamicsWorld *world, btScalar timeStep);

class InternalTickCallback {
protected:
	btDynamicsWorld *mWorld;
	bool mIsPreTick;
public:
	InternalTickCallback(btDynamicsWorld *dynamicsWorld = NULL, bool isPreTick = false);
	virtual void onInternalTick(btDynamicsWorld *dynamicsWorld, btScalar timeStep) { }
	void detach();
	void attach(btDynamicsWorld *dynamicsWorld, bool isPreTick);
	void attach();
	static void detach(btDynamicsWorld *dynamicsWorld, bool isPreTick);
};

static void InternalTickCallback_CB(btDynamicsWorld *world, btScalar timeStep) {
	InternalTickCallback *cb = static_cast<InternalTickCallback *>(world->getWorldUserInfo());
	cb->onInternalTick(world, timeStep);
}

#endif // InternalTickCallback_H
